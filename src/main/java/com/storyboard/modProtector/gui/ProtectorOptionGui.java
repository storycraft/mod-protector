package com.storyboard.modProtector.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.storyboard.modProtector.config.IConfigFile;
import com.storyboard.modProtector.config.json.JsonConfigFile;
import com.storyboard.modProtector.profile.ProfileManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class ProtectorOptionGui extends GuiScreen {

    private static final int DONE_BTN = 8814;
    private static final int NEW_CONFIG_FIELD = 1234;

    private Minecraft client;

    private GuiScreen lastGui;

    private ProfileManager profileManager;

    private JsonConfigFile profileConfig;
    private List<ConfigInfo> profileInfoList;

    private ConfigInfo selectedInfo;

    

    private boolean isConfigLoaded;



    private ProfileListGui listGui;

    private GuiTextField newConfigField;

    public ProtectorOptionGui(Minecraft client, GuiScreen lastGui, ProfileManager profileManager) {
        super();

        this.client = client;
        
        this.lastGui = lastGui;

        this.profileManager = profileManager;

        this.profileConfig = null;
        this.profileInfoList = new ArrayList<>();

        this.selectedInfo = null;

        this.isConfigLoaded = false;

        initConfig();
    }

    protected void initConfig() {
        profileConfig = profileManager.reloadProfileConfig();

        profileManager.loadAllProfile().run().thenRun(() -> {

            Map<String, JsonConfigFile> profileMap = profileManager.getProfileMap();

            profileInfoList.clear();

            String activeProfileName = "";

            if (profileConfig.contains("selected_profile")) {
                try {
                    activeProfileName = profileConfig.get("selected_profile").getAsString();
                } catch (Exception e) {

                }
            }

            for (String name : profileMap.keySet()) {
                ConfigInfo info = new ConfigInfo(name, profileMap.get(name));

                profileInfoList.add(info);

                if (name.equals(activeProfileName)) {
                    selectedInfo = info;
                }
            }

            this.isConfigLoaded = true;
        });
    }

    protected void saveProfileConfig() {
        if (selectedInfo != null) {
            profileConfig.set("selected_profile", selectedInfo.name);
        }

        profileManager.saveProfileConfig(profileConfig).getSync();

        for (ConfigInfo changedInfo : profileInfoList) {
            if (changedInfo.isChanged()) {
                profileManager.saveProfile(changedInfo.name, changedInfo.config).getSync();
            }
        }
    }

    protected boolean createNewConfig(String name) {
        if (profileManager.hasProfile(name)) {
            return false;
        }

        JsonConfigFile configFile = profileManager.getProfile(name).getSync();
        ConfigInfo configInfo = new ConfigInfo(name, configFile);

        profileInfoList.add(configInfo);

        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean wasFocused = newConfigField.isFocused();

        newConfigField.mouseClicked(mouseX, mouseY, mouseButton);
        if (!wasFocused && wasFocused != this.newConfigField.isFocused()) {
            newConfigField.setTextColor(0xffffffff);
            newConfigField.setText("");
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        this.listGui = new ProfileListGui();

        addButton(new GuiButton(DONE_BTN, 135 + (this.width - 135) / 2 - 75, this.height - 40, 150, 20, "Done"));

        newConfigField = new GuiTextField(NEW_CONFIG_FIELD, fontRenderer, 5, this.height - 35, 125, 20);

        newConfigField.setFocused(false);
        newConfigField.setCanLoseFocus(true);
        newConfigField.setText("Ex) new-config");
        newConfigField.setTextColor(0xff888888);

        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { //ESC
            this.saveProfileConfig();
        } else if (keyCode == 28) { // ENTER
            if (newConfigField.isFocused() && !newConfigField.getText().isEmpty()) {

                String newConfigName = newConfigField.getText() + ".profile";

                if (!createNewConfig(newConfigName)) {
                    for (ConfigInfo configInfo : profileInfoList) {
                        if (configInfo.name.equals(newConfigName)) {
                            selectedInfo = configInfo;
                            break;
                        }
                    }
                }

                newConfigField.setFocused(false);
            }
        } else if (newConfigField.isFocused()) {
            newConfigField.writeText(typedChar + "");

            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == DONE_BTN) {
            this.saveProfileConfig();

            client.displayGuiScreen(lastGui);
            return;
        }
 
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRenderer, "ModProtector settings", this.width / 2, 15, 0xffffffff);

        if (!isConfigLoaded) {
            this.drawCenteredString(this.fontRenderer, "Loading config and profiles...", this.width / 2,
                    this.height / 2, 0xffffffff);
        } else {
            this.listGui.drawScreen(mouseX, mouseY, partialTicks);
            this.newConfigField.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public class ProfileListGui extends GuiScrollingList {

        public ProfileListGui() {
            super(client, 125, ProtectorOptionGui.this.height, 35, ProtectorOptionGui.this.height - 50, 5, 20, ProtectorOptionGui.this.width, ProtectorOptionGui.this.height);
        }

        @Override
        protected int getSize() {
            return profileInfoList.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {
            if (profileInfoList.size() <= index) {
                return;
            }

            selectedInfo = profileInfoList.get(index);
        }

        @Override
        protected boolean isSelected(int index) {
            return profileInfoList.size() > index && profileInfoList.get(index) == selectedInfo;
        }

        @Override
        protected void drawBackground() {
            
        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
            if (profileInfoList.size() <= slotIdx) {
                return;
            }

            ConfigInfo info = profileInfoList.get(slotIdx);

            fontRenderer.drawString(fontRenderer.trimStringToWidth(info.getName(), this.listWidth - 6), this.left + 3, slotTop + 4, 0xffffffff);
        }

    }

    public static class ConfigInfo {

        private String name;

        private JsonConfigFile config;

        private boolean changed;

        public ConfigInfo(String name, JsonConfigFile config) {
            this.name = name;
            this.config = config;

            this.changed = false;
        }

        public String getName() {
            return name;
        }
        
        public IConfigFile getConfig() {
            return config;
        }

        public void markChanged() {
            this.changed |= true;
        }
        
        public boolean isChanged() {
            return changed;
        }

    }
}