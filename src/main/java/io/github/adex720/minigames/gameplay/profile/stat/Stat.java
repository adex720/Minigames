package io.github.adex720.minigames.gameplay.profile.stat;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.JsonHelper;

public record Stat(int id, String name, String description, boolean onLeaderboard) {

    public static Stat fromJson(JsonObject json) {
        int id = JsonHelper.getInt(json, "id");
        String name = JsonHelper.getString(json, "name");
        String description = JsonHelper.getString(json, "description");
        boolean onLeaderboard = JsonHelper.getBoolean(json, "on_leaderboard");

        return new Stat(id, name, description, onLeaderboard);
    }

}
