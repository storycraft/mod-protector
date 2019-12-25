package com.storyboard.modProtector;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.storyboard.modProtector.inject.HandshakeInjector;
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

    private ModListManager modListManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        client = Minecraft.getMinecraft();
        modListManager = new ModListManager(client, logger);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Mod loaded successfully");
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        injector = new HandshakeInjector(this);
        MinecraftForge.EVENT_BUS.register(injector);
    }

    public Minecraft getClient() {
        return this.client;
    }
    
    public Logger getLogger() {
        return logger;
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
