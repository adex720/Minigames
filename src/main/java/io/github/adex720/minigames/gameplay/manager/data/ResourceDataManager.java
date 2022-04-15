package io.github.adex720.minigames.gameplay.manager.data;

import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.DataManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 * Loads resource Json files.
 * File contents are also cached
 */
public class ResourceDataManager extends DataManager {

    private final HashMap<String, JsonElement> CACHED_JSON;

    public ResourceDataManager(MinigamesBot bot) {
        super(bot, "resource-data-manager");
        CACHED_JSON = new HashMap<>();
    }

    @Override
    public JsonElement loadJson(String name) {
        JsonElement cached = CACHED_JSON.get(name);
        if (cached != null) return cached;

        String path = name + ".json";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(inputStream);
        JsonElement json = gson.fromJson(reader, JsonElement.class);

        CACHED_JSON.put(name, json);

        return json;
    }

    @Override
    public boolean saveJson(JsonElement json, String name) {
        bot.getLogger().error("Tried to save file on resources!");
        return false;
    }

    public void clearCache() {
        CACHED_JSON.clear();
        bot.getLogger().info("Cleared resource json cache");
    }
}
