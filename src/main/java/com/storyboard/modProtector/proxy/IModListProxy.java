package com.storyboard.modProtector.proxy;

import java.util.List;

import net.minecraftforge.fml.common.ModContainer;

public interface IModListProxy {

    List<ModContainer> getModList();

}