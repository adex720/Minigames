package io.github.adex720.minigames.gameplay.manager.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.DataManager;
import io.github.adex720.minigames.util.JsonHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class BotDataManager extends DataManager {

    private final String databaseUri;
    private final String username;
    private final String password;
    // TODO: create database inside vm

    public BotDataManager(MinigamesBot bot, JsonObject databaseDetails) throws SQLException {
        super(bot, "data-manager");

        databaseUri = JsonHelper.getStringOrThrow(databaseDetails, "uri", "Invalid database config json! Missing uri") + "\\";
        username = JsonHelper.getStringOrThrow(databaseDetails, "username", "Invalid database config json! Missing username");
        password = JsonHelper.getStringOrThrow(databaseDetails, "password", "Invalid database config json! Missing password");

    }

    @Override
    public JsonElement loadJson(String name) {
        String path = databaseUri + name + ".json";

        JsonElement json;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            json = gson.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            bot.getLogger().error(e.getMessage());
            return new JsonObject();
        }

        return json;
    }

    @Override
    public boolean saveJson(JsonElement json, String name) {
        String path = databaseUri + name + ".json";

        try {
            FileWriter writer = new FileWriter(path);
            gson.toJson(json, writer);
            writer.flush();
            return true;
        } catch (Exception e) {
            bot.getLogger().error(e.getMessage());
            return false;
        }
    }
}
