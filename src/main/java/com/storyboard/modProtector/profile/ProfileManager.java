package com.storyboard.modProtector.profile;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.storyboard.modProtector.config.ConfigManager;
import com.storyboard.modProtector.config.json.JsonConfigFile;
import com.storyboard.modProtector.config.json.JsonConfigPrettyFile;
import com.storyboard.modProtector.util.AsyncTask;
import com.storyboard.modProtector.util.Parallel;

public class ProfileManager {

    private static final String PROFILE_CONFIG_NAME = "config.json";

    private ConfigManager configManager;

    private JsonConfigFile profileConfig;

    private Map<String, JsonConfigFile> profileMap;

    public ProfileManager(ConfigManager configManager) {
        this.configManager = configManager;

        profileMap = new ConcurrentHashMap<>();

        profileConfig = new JsonConfigFile();

        configManager.getProfileStorage().createStorageDirectory();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public JsonConfigFile getProfileConfig() {
        return profileConfig;
    }
    
    public Map<String, JsonConfigFile> getProfileMap() {
        return profileMap;
    }

    public boolean hasProfile(String name) {
        return profileMap.containsKey(name);
    }

    public JsonConfigFile reloadProfileConfig() {
        configManager.loadConfig(profileConfig, PROFILE_CONFIG_NAME).getSync();

        return profileConfig;
    }

    public AsyncTask<JsonConfigFile> getProfile(String name) {
        return new AsyncTask<>(() -> {
            if (hasProfile(name)) {
                return profileMap.get(name);
            }

            return reloadProfile(name).getSync();
        });
    }

    public AsyncTask<JsonConfigFile> reloadProfile(String name) {
        return new AsyncTask<>(() -> {
            JsonConfigFile previousConfig = profileMap.remove(name);

            JsonConfigFile profileConfig = previousConfig != null ? previousConfig : new JsonConfigPrettyFile();

            configManager.loadProfile(profileConfig, name).getSync();

            profileMap.put(name, profileConfig);

            return profileConfig;
        });
    }

    public AsyncTask<Void> loadAllProfile() {
        return new AsyncTask<>(() -> {
            File profileFolder = configManager.getProfileStorage().getStorageFolder();

            String[] profileFiles = profileFolder.list((File dir, String name) -> {
                return name.endsWith(".profile");
            });
        
            profileMap.clear();
        
            Parallel.forEach(profileFiles, (profileName) -> {
                profileMap.put(profileName, reloadProfile(profileName).getSync());
                return null;
            });

            return null;
        });
    }

    public AsyncTask<Void> saveProfile(String name, JsonConfigFile profile) {
        return configManager.saveProfile(profile, name);
    }

    public void setSelectedProfile(String name) {
        profileConfig.set("selectedProfile", name);
    }

    public String getSelectedProfile() {
        try {
            return profileConfig.get("selectedProfile").getAsString();
        } catch (Exception e) {
            profileConfig.set("selectedProfile", "");

            return "";
        }
    }

    public AsyncTask<Void> saveProfileConfig() {
        return configManager.saveConfig(profileConfig, PROFILE_CONFIG_NAME);
    }

}