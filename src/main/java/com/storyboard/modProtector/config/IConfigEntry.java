package com.storyboard.modProtector.config;

import java.util.List;

public interface IConfigEntry<T extends IConfigEntry<T>> {
    
    void set(String key, T value);
    void set(String key, byte value);
    void set(String key, int value);
    void set(String key, short value);
    void set(String key, long value);
    void set(String key, double value);
    void set(String key, float value);
    void set(String key, boolean value);
    void set(String key, String value);
    <E> void set(String key, List<E> value);
    void set(String key, Object value);

    boolean contains(String key);

    Object get(String key);

    T createEntry();
    T getObject(String key);
}
