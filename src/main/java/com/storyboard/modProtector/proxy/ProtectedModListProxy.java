package com.storyboard.modProtector.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storyboard.modProtector.ModProtector;
import com.storyboard.modProtector.config.json.JsonConfigEntry;
import com.storyboard.modProtector.wrapper.FakeModContainer;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ProtectedModListProxy implements IModListProxy {

    private Minecraft mc;
    
    private Map<String, String> modListToAdd;
    private List<String> modListHidden;

    public ProtectedModListProxy(Minecraft mc) {
        this(mc, new HashMap<>(), new ArrayList<>());
    }

    public ProtectedModListProxy(Minecraft mc, Map<String, String> modListToAdd, List<String> modListHidden) {
        this.mc = mc;

        this.modListToAdd = modListToAdd;
        this.modListHidden = modListHidden;
    }

    @Override
    public List<ModContainer> getModList() {
        List<ModContainer> list = new ArrayList<>();

        Iterator<ModContainer> modIter = Loader.instance().getModList().iterator();

        while (modIter.hasNext()) {
            ModContainer container = modIter.next();

            if (ModProtector.MODID.equals(container.getModId())) {
                continue;
            }

            if (modListHidden.contains(container.getModId())) {
                continue;
            }

            list.add(container);
        }

        for (String id : modListToAdd.keySet()) {
            list.add(new FakeModContainer(id, modListToAdd.get(id)));
        }

        return list;
    }

    @Override
    public void fromConfig(JsonConfigEntry entry) {
        modListToAdd.clear();
        modListHidden.clear();

        JsonArray appendList;
        try {
            appendList = entry.get("modListAppend").getAsJsonArray();
        } catch (Exception e) {
            appendList = new JsonArray();
        }

        JsonArray hiddenList;
        try {
            hiddenList = entry.get("modListHidden").getAsJsonArray();
        } catch (Exception e) {
            hiddenList = new JsonArray();
        }

        for (JsonElement element : appendList) {
            if (element instanceof JsonObject) {
                JsonObject obj = element.getAsJsonObject();
                try {
                    String modId = obj.get("modId").getAsString();
                    String modVersion = obj.get("modVersion").getAsString();

                    modListToAdd.put(modId, modVersion);
                } catch (Exception e) {
                    
                }
            }
        }

        for (JsonElement element : hiddenList) {
            try {
                String modId = element.getAsString();

                modListHidden.add(modId);
            } catch (Exception e) {
                
            }
        }
    }

}