package com.storyboard.proxy;

import java.util.List;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class DefaultModListProxy implements IModListProxy {

    @Override
    public List<ModContainer> getModList() {
        return Loader.instance().getModList();
    }

}