package com.storyboard.modProtector.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storyboard.modProtector.config.json.JsonConfigEntry;
import com.storyboard.modProtector.wrapper.FakeModContainer;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.ModContainer;

public class CustomModListProxy implements IModListProxy {

    private Minecraft mc;

    private Map<String, String> modList;

    public CustomModListProxy(Minecraft mc) {
        this(mc, new HashMap<>());   
	}

    public CustomModListProxy(Minecraft mc, Map<String, String> modList) {
        this.mc = mc;
        this.modList = modList;
    }

	@Override
    public List<ModContainer> getModList() {
        List<ModContainer> list = new ArrayList<>();

        for (String id : modList.keySet()) {
            list.add(new FakeModContainer(id, modList.get(id)));
        }

        return list;
    }

    @Override
    public void fromConfig(JsonConfigEntry entry) {
        modList.clear();

        JsonArray listArray;
        try {
            listArray = entry.get("modList").getAsJsonArray();
        } catch (Exception e) {
            listArray = new JsonArray();
        }

        for (JsonElement element : listArray) {
            if (element instanceof JsonObject) {
                JsonObject obj = element.getAsJsonObject();
                try {
                    String modId = obj.get("modId").getAsString();
                    String modVersion = obj.get("modVersion").getAsString();

                    modList.put(modId, modVersion);
                } catch (Exception e) {
                    
                }
            }
        }
    }

}