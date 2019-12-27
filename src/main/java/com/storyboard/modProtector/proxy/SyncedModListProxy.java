package com.storyboard.modProtector.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.storyboard.modProtector.config.json.JsonConfigEntry;
import com.storyboard.modProtector.util.Reflect;
import com.storyboard.modProtector.wrapper.FakeModContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import cpw.mods.fml.client.ExtendedServerListData;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ModContainer;

public class SyncedModListProxy implements IModListProxy {

    private static Reflect.WrappedField<Map<ServerData, ExtendedServerListData>, FMLClientHandler> serverDataTagField;

    static {
        serverDataTagField = Reflect.getField(FMLClientHandler.class, "serverDataTag");
    }

    private Minecraft mc;

    public SyncedModListProxy(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public List<ModContainer> getModList() {
        ServerData serverData = mc.func_147104_D();

        if (serverData == null) {
            return Lists.newArrayList();
        }

        Map<ServerData, ExtendedServerListData> map = serverDataTagField.get(FMLClientHandler.instance());

        if (!map.containsKey(serverData)) {
            return Lists.newArrayList();
        }

        ExtendedServerListData extendedData = map.get(serverData);

        List<ModContainer> list = new ArrayList<>();

        for (String id : extendedData.modData.keySet()) {
            list.add(new FakeModContainer(id, extendedData.modData.get(id)));
        }

        System.out.println("Syncing modList data with server");

        return list;
    }

    @Override
    public void fromConfig(JsonConfigEntry entry) {
        
    }
    
}