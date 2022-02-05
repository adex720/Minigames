package io.github.adex720.minigames.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public abstract class DataManager {

    protected final Gson gson;

    public DataManager() {
        gson = new Gson();
    }

    public abstract JsonElement loadJson(String name);

    public abstract boolean saveJson(JsonElement json, String name);

}
