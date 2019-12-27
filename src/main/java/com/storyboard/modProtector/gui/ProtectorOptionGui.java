package com.storyboard.modProtector.gui;

import java.io.IOException;

import com.storyboard.modProtector.config.ConfigManager;

import net.minecraft.client.gui.GuiScreen;

public class ProtectorOptionGui extends GuiScreen {

    private GuiScreen lastGui;

    private ConfigManager configManager;

    public ProtectorOptionGui(GuiScreen lastGui, ConfigManager configManager) {
        this.lastGui = lastGui;

        this.configManager = configManager;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "ModProtector Settings", this.width / 2, 15, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}