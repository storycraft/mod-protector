package com.storyboard.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.storyboard.ModProtector;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class DefaultModListProxy implements IModListProxy {

    @Override
    public List<ModContainer> getModList() {
        List<ModContainer> list = new ArrayList<>(Loader.instance().getModList());

        Iterator<ModContainer> modIter = list.iterator();

        while (modIter.hasNext()) {
            ModContainer container = modIter.next();

            if (ModProtector.MODID.equals(container.getModId())) {
                modIter.remove();
                break;
            }
        }

        return list;
    }

}