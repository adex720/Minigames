package io.github.adex720.minigames.gameplay.manager.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.DataManager;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ResourceDataManager extends DataManager {

    private final HashMap<String, JsonElement> CACHED;

    public ResourceDataManager(MinigamesBot bot) {
        super(bot, "resource-data-manager");
        CACHED = new HashMap<>();
    }

    @Override
    public JsonElement loadJson(String name) {
        JsonElement cached = CACHED.get(name);
        if (cached != null) return cached;

        String path = "src/main/resources/" + name + ".json";

        JsonElement json;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            json = gson.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            bot.getLogger().error(e.getMessage());
            return new JsonObject();
        }

        CACHED.put(name, json);

        return json;
    }

    @Override
    public boolean saveJson(JsonElement json, String name) {
        bot.getLogger().error("Tried to save file on resources!");
        return false;
    }

    public void clearCache(){
        CACHED.clear();
        bot.getLogger().info("Cleared resource json cache");
    }
}
