package io.github.adex720.minigames.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

/**
 * This class can save and load json files.
 * */
public abstract class DataManager extends Manager {

    protected final Gson gson;

    public DataManager(MinigamesBot bot, String name) {
        super(bot, name);
        gson = new Gson();
    }

    public abstract JsonElement loadJson(String name);

    public abstract boolean saveJson(JsonElement json, String name);

}
