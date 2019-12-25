package com.storyboard;

import com.storyboard.proxy.DefaultModListProxy;
import com.storyboard.proxy.IModListProxy;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;

public class ModListManager {

    private static IModListProxy defaultModListProxy;

    static {
        defaultModListProxy = new DefaultModListProxy();
    }

    public static IModListProxy getDefaultModListProxy() {
        return defaultModListProxy;
    }

    private Logger logger;

    private Minecraft client;

    private IModListProxy modListProxy;

    public ModListManager(Minecraft mc, Logger logger) {
        this.client = mc;
        this.logger = logger;
    }

    public Minecraft getClient() {
        return this.client;
    }
    
    public Logger getLogger() {
        return logger;
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

        return ModListManager.defaultModListProxy;
    }
}