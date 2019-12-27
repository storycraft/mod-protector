package com.storyboard.modProtector;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import com.storyboard.modProtector.config.ConfigManager;
import com.storyboard.modProtector.gui.GuiManager;
import com.storyboard.modProtector.inject.HandshakeInjector;
import com.storyboard.modProtector.profile.ProfileManager;
import com.storyboard.modProtector.proxy.IModListProxy;

import org.apache.logging.log4j.Logger;

@Mod(modid = ModProtector.MODID, name = ModProtector.NAME, version = ModProtector.VERSION, clientSideOnly = true)
public class ModProtector {

    public static final String MODID = "mod-protector";
    public static final String NAME = "ModProtector";
    public static final String VERSION = "1.0";

    private Logger logger;
    private Minecraft client;

    private HandshakeInjector injector;

    private ConfigManager configManager;
    private ModListManager modListManager;

    private ProfileManager profileManager;
    
    private GuiManager guiManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        client = Minecraft.getMinecraft();

        configManager = new ConfigManager(new File(event.getModConfigurationDirectory(), ModProtector.MODID), logger);
        profileManager = new ProfileManager(configManager);

        modListManager = new ModListManager(client, profileManager, logger);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        injector = new HandshakeInjector(this);

        guiManager = new GuiManager(this);

        MinecraftForge.EVENT_BUS.register(injector);
        MinecraftForge.EVENT_BUS.register(guiManager);

        logger.info("Mod loaded successfully");
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    public Minecraft getClient() {
        return this.client;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public HandshakeInjector getInjector() {
        return injector;
    }

    public ModListManager getModListManager() {
        return modListManager;
    }

    public IModListProxy getModListProxy() {
        return modListManager.getModListProxy();
    }

    public void setModListProxy(IModListProxy proxy) {
        modListManager.setModListProxy(proxy);
    }

    public IModListProxy getOrDefaultModListProxy() {
        return modListManager.getOrDefaultModListProxy();
    }
    
    
}
