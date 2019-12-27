package com.storyboard.modProtector.proxy;

import java.util.List;

import com.storyboard.modProtector.config.json.JsonConfigEntry;

import cpw.mods.fml.common.ModContainer;

public interface IModListProxy {

    List<ModContainer> getModList();

    void fromConfig(JsonConfigEntry entry);

}