package com.storyboard.modProtector.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.storyboard.modProtector.ModProtector;
import com.storyboard.modProtector.wrapper.FakeModContainer;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ProtectedModListProxy implements IModListProxy {
    
    private Map<String, String> modListToAdd;
    private Map<String, String> modListToRemove;

    public ProtectedModListProxy(Map<String, String> modListToAdd, Map<String, String> modListToRemove) {
        this.modListToAdd = modListToAdd;
        this.modListToRemove = modListToRemove;
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

            if (this.modListToRemove.containsKey(container.getModId())) {
                continue;
            }

            list.add(container);

            System.out.println("allow acessing " + container.getModId() + " to server");
        }

        for (String id : modListToAdd.keySet()) {
            list.add(new FakeModContainer(id, modListToAdd.get(id)));
        }

        return list;
    }

}