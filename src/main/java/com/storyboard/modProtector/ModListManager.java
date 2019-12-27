package com.storyboard.modProtector;

import com.storyboard.modProtector.config.json.JsonConfigEntry;
import com.storyboard.modProtector.profile.ProfileManager;
import com.storyboard.modProtector.proxy.DefaultModListProxy;
import com.storyboard.modProtector.proxy.IModListProxy;
import com.storyboard.modProtector.proxy.ProxyType;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;

public class ModListManager {

    private IModListProxy defaultModListProxy;

    private Logger logger;

    private Minecraft client;

    private ProfileManager profileManager;

    private IModListProxy modListProxy;

    public ModListManager(Minecraft mc, ProfileManager profileManager, Logger logger) {
        this.client = mc;
        this.logger = logger;

        this.profileManager = profileManager;

        defaultModListProxy = new DefaultModListProxy(mc);
        
        modListProxy = null;
    }

    public Minecraft getClient() {
        return this.client;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public IModListProxy getDefaultModListProxy() {
        return defaultModListProxy;
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

        return defaultModListProxy;
    }

    public int getProxyProfileType(JsonConfigEntry entry) {
        try {
            return entry.get("type").getAsInt();
        } catch (Exception e) {
            logger.error("Error while reading proxy profile type. Setting default. " + e);
            
            entry.set("type", ProxyType.DEFAULT.getId());

            return ProxyType.DEFAULT.getId();
        }
    }

    public String getProxyProfileDescription(JsonConfigEntry entry) {
        try {
            return entry.get("desc").getAsString();
        } catch (Exception e) {
            logger.error("Error while reading proxy profile description. Setting none. " + e);
            
            entry.set("desc", "");

            return "";
        }
    }

    public void setFromProxyProfile(JsonConfigEntry entry) {
        try {
            int type = getProxyProfileType(entry);

            JsonConfigEntry settingsEntry = entry.getObject("settings");

            if (settingsEntry == null) {
                entry.set("settings", settingsEntry = entry.createEntry());
            }

            IModListProxy proxy = ProxyType.findById(type).createNew(client);

            proxy.fromConfig(settingsEntry);

            this.setModListProxy(proxy);

        } catch (Exception e) {
            logger.error("Error while applying proxy profile. Applying none. " + e);
            this.setModListProxy(null);
        }

    }
}