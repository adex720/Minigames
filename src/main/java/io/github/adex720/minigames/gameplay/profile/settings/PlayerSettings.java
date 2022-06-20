package io.github.adex720.minigames.gameplay.profile.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author adex720
 */
public class PlayerSettings implements IdCompound, JsonSavable {

    private final long userId;

    private final HashMap<Setting, Boolean> VALUES;

    public PlayerSettings(long userId, SettingValue... settings) {
        this.userId = userId;
        VALUES = new HashMap<>();

        for (SettingValue settingValue : settings) {
            VALUES.put(settingValue.setting(), settingValue.value());
        }
    }

    @Override
    public Long getId() {
        return userId;
    }

    /**
     * Settings are stored as {@link JsonArray}. Use {@link PlayerSettings#getAsJsonArray()} instead.
     */
    @Override
    public JsonObject getAsJson() {
        return null;
    }

    public JsonArray getAsJsonArray() {
        JsonArray json = new JsonArray();

        for (Map.Entry<Setting, Boolean> setting : VALUES.entrySet()) {
            JsonObject settingJson = new JsonObject();
            settingJson.addProperty("id", setting.getKey().id());
            settingJson.addProperty("value", setting.getValue());
            json.add(settingJson);
        }

        return json;
    }

    public void set(Setting setting, boolean value) {
        if (setting.defaultValue() == value) { // Remove setting from map if it's set to its default value
            VALUES.remove(setting);
            return;
        }

        VALUES.put(setting, value);
    }

    public boolean getValue(Setting setting) {
        return VALUES.getOrDefault(setting, setting.defaultValue());
    }

    public static PlayerSettings fromJson(MinigamesBot bot, JsonArray json, long userId) {

        SettingValue[] settingValues = new SettingValue[json.size()];
        int i = 0;
        for (JsonElement settingJsonElement : json) {
            JsonObject settingJson = settingJsonElement.getAsJsonObject();

            int settingId = JsonHelper.getInt(settingJson, "id");
            Setting setting = bot.getSettingsList().get(settingId);
            boolean value = JsonHelper.getBoolean(settingJson, "value");

            settingValues[i] = new SettingValue(setting, value);
            i++;
        }

        return new PlayerSettings(userId, settingValues);
    }
}
