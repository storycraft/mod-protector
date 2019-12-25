package com.storyboard.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.storyboard.wrapper.FakeModContainer;

import net.minecraftforge.fml.common.ModContainer;

public class CustomModListProxy implements IModListProxy {

    private Map<String, String> modList;

    public CustomModListProxy(Map<String, String> modList) {
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

}