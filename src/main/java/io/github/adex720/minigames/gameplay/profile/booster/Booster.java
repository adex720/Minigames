package io.github.adex720.minigames.gameplay.profile.booster;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.util.JsonHelper;

public class Booster implements JsonSavable<Booster> {

    public final BoosterRarity rarity;
    public final long expiration;

    public Booster(BoosterRarity rarity, long expiration) {
        this.rarity = rarity;
        this.expiration = expiration;
    }

    public static Booster fromJson(JsonElement json) {
        JsonObject boosterJson = json.getAsJsonObject();

        BoosterRarity rarity = BoosterRarity.get(JsonHelper.getInt(boosterJson, "booster_id"));
        long expiration = JsonHelper.getLong(boosterJson, "expiration");

        return new Booster(rarity, expiration);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("id", rarity.id);
        json.addProperty("expiration", expiration);

        return json;
    }
}