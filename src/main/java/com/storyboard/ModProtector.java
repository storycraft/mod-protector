package com.storyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.storyboard.inject.HandshakeInjector;
import com.storyboard.proxy.DefaultModListProxy;
import com.storyboard.proxy.IModListProxy;
import com.storyboard.proxy.SkippingModListProxy;

import org.apache.logging.log4j.Logger;

@Mod(modid = ModProtector.MODID, name = ModProtector.NAME, version = ModProtector.VERSION, clientSideOnly = true)
public class ModProtector {

    private static IModListProxy defaultModListProxy;

    public static final String MODID = "mod-protector";
    public static final String NAME = "ModProtector";
    public static final String VERSION = "1.0";

    static {
        defaultModListProxy = new DefaultModListProxy();
    }

    public static IModListProxy getDefaultModListProxy() {
        return defaultModListProxy;
    }

    private static Logger logger;

    private Minecraft client;

    private HandshakeInjector injector;

    private IModListProxy modListProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        client = Minecraft.getMinecraft();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.modListProxy = new SkippingModListProxy(this);

        injector = new HandshakeInjector(this);
        MinecraftForge.EVENT_BUS.register(injector);
        
        logger.info("Mod loaded successfully");
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

    public IModListProxy getModListProxy() {
        return this.modListProxy;
    }

    public void setModListProxy(IModListProxy proxy) {
        this.modListProxy = proxy;
    }

    public IModListProxy getOrDefaultModListProxy() {
        if (this.modListProxy != null) {
            return this.modListProxy;
        }

        return ModProtector.defaultModListProxy;
    }
    
    
}
