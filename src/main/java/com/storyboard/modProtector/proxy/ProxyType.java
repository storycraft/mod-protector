package com.storyboard.modProtector.proxy;

import java.util.function.Function;

import net.minecraft.client.Minecraft;

public enum ProxyType {

    DEFAULT(0, (Minecraft mc) -> new DefaultModListProxy(mc)),
    CUSTOM(1, (Minecraft mc) -> new CustomModListProxy(mc)),
    PROTECTED(2, (Minecraft mc) -> new ProtectedModListProxy(mc)),
    SYNCED(3, (Minecraft mc) -> new SyncedModListProxy(mc));
    
    private int id;
    private Function<Minecraft, IModListProxy> constructor;

    ProxyType(int id, Function<Minecraft, IModListProxy> constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    public int getId() {
        return id;
    }

    public IModListProxy createNew(Minecraft mc) {
        return constructor.apply(mc);
    }

    public static ProxyType findById(int id) {
        for (ProxyType type : values()) {
            if (type.id == id) {
                return type;
            }
        }

        return DEFAULT;
    }

}