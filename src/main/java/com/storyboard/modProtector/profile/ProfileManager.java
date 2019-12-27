package com.storyboard.modProtector.profile;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.storyboard.modProtector.config.ConfigManager;
import com.storyboard.modProtector.config.IConfigFile;
import com.storyboard.modProtector.config.json.JsonConfigFile;
import com.storyboard.modProtector.config.json.JsonConfigPrettyFile;
import com.storyboard.modProtector.util.AsyncTask;
import com.storyboard.modProtector.util.Parallel;

public class ProfileManager {

    private static final String GLOBAL_CONFIG_NAME = "config.json";

    private ConfigManager configManager;

    private JsonConfigFile globalConfig;

    private Map<String, IConfigFile> profileList;

    public ProfileManager(ConfigManager configManager) {
        this.configManager = configManager;

        profileList = new ConcurrentHashMap<>();

        globalConfig = new JsonConfigFile();

        configManager.getProfileStorage().createStorageDirectory();
        reloadProfileConfig();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public JsonConfigFile getProfileConfig() {
        return globalConfig;
    }

    public boolean hasProfile(String name) {
        return profileList.containsKey(name);
    }

    public IConfigFile reloadProfileConfig() {
        configManager.loadConfig(globalConfig, GLOBAL_CONFIG_NAME);
        return globalConfig;
    }

    public AsyncTask<IConfigFile> getProfile(String name) {
        return new AsyncTask<>(() -> {
            if (hasProfile(name)) {
                return profileList.get(name);
            }

            return reloadProfile(name).getSync();
        });
    }

    public AsyncTask<IConfigFile> reloadProfile(String name) {
        return new AsyncTask<>(() -> {
            IConfigFile previousConfig = profileList.remove(name);

            IConfigFile profileConfig = previousConfig != null ? previousConfig : new JsonConfigPrettyFile();

            configManager.loadConfig(profileConfig, name).getSync();

            profileList.put(name, profileConfig);

            return profileConfig;
        });
    }

    public AsyncTask<Void> loadAllProfile() {
        return new AsyncTask<>(() -> {
            File profileFolder = configManager.getProfileStorage().getStorageFolder();

            String[] profileFiles = profileFolder.list((File dir, String name) -> {
                return name.endsWith(".profile");
            });
        
            profileList.clear();
        
            Parallel.forEach(profileFiles, (profileName) -> {
                profileList.put(profileName, reloadProfile(profileName).getSync());
                return null;
            });

            return null;
        });
    }

    public AsyncTask<Void> saveProfile(IConfigFile profileConfig, String name) {
        return configManager.saveConfig(profileConfig, name);
    }

}