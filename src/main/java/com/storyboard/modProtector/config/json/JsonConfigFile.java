package com.storyboard.modProtector.config.json;

import com.google.gson.*;
import com.storyboard.modProtector.config.IConfigFile;

import java.io.*;

public class JsonConfigFile extends JsonConfigEntry implements IConfigFile {

    public JsonConfigFile(){

    }

    @Override
    public void load(InputStream is) {
        try {
            setJsonObject(new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject());
        } catch (Exception e) {
            //create new file when corrupted or file is not exists
            setJsonObject(new JsonObject());
        }
    }

    @Override
    public void save(OutputStream os) throws IOException {
        try {
            Gson gson = new Gson();

            os.write(gson.toJson(getJsonObject()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
