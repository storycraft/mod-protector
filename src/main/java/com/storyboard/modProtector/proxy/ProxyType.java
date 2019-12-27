package com.storyboard.modProtector.proxy;

import java.util.function.Function;

import net.minecraft.client.Minecraft;

public enum ProxyType {

    DEFAULT(0, "Default", (Minecraft mc) -> new DefaultModListProxy(mc)),
    CUSTOM(1, "Custom", (Minecraft mc) -> new CustomModListProxy(mc)),
    PROTECTED(2, "Inspection", (Minecraft mc) -> new ProtectedModListProxy(mc)),
    SYNCED(3, "Synced", (Minecraft mc) -> new SyncedModListProxy(mc));
    
    private int id;
    private String name;
    private Function<Minecraft, IModListProxy> constructor;

    ProxyType(int id, String name, Function<Minecraft, IModListProxy> constructor) {
        this.id = id;
        this.name = name;
        this.constructor = constructor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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