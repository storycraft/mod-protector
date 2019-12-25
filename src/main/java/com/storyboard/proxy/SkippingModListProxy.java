package com.storyboard.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.storyboard.ModProtector;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class SkippingModListProxy implements IModListProxy {

    private ModProtector mod;

    public SkippingModListProxy(ModProtector mod) {
        this.mod = mod;
    }

    @Override
    public List<ModContainer> getModList() {
        List<ModContainer> list = new ArrayList<>();

        Iterator<ModContainer> modIter = Loader.instance().getModList().iterator();

        while (modIter.hasNext()) {
            ModContainer container = modIter.next();

            if ("mod-protector".equals(container.getModId())) {
                continue;
            }

            list.add(container);

            System.out.println("allow acessing " + container.getModId() + " to server");
        }


        return list;
    }

}