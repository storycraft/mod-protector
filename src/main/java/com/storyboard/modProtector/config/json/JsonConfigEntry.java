package com.storyboard.modProtector.config.json;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.storyboard.modProtector.config.IConfigEntry;

public class JsonConfigEntry implements IConfigEntry<JsonConfigEntry> {

    private JsonObject jsonObject;

    public JsonConfigEntry() {
        this.jsonObject = new JsonObject();
    }

    public JsonConfigEntry(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    protected void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void set(String key, JsonConfigEntry value) {
        getJsonObject().add(key, value.getJsonObject());
    }

    @Override
    public void set(String key, byte value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, int value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, short value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, long value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, double value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, float value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, boolean value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, String value) {
        getJsonObject().addProperty(key, value);
    }

    @Override
    public void set(String key, Object value) {
        if (value instanceof JsonConfigEntry)
            set(key, (JsonConfigEntry) value);
        if (value instanceof JsonElement)
            getJsonObject().add(key, (JsonElement) value);
        else 
            set(key, value.toString());
    }

    @Override
    public boolean contains(String key) {
        return getJsonObject().has(key);
    }

    @Override
    public JsonElement get(String key) {
        return getJsonObject().get(key);
    }

    @Override
    public JsonConfigEntry getObject(String key) {
        JsonElement element = get(key);
        if (element == null || !element.isJsonObject())
            return null;
        return new JsonConfigEntry(element.getAsJsonObject());
    }

    @Override
    public <E>void set(String key, List<E> value) {
        set(key, new Gson().toJsonTree(value,
        new TypeToken<List<E>>() {
        }.getType()));
	}

    @Override
    public JsonConfigEntry createEntry() {
        return new JsonConfigEntry();
    }

}
