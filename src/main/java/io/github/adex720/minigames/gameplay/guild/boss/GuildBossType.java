package io.github.adex720.minigames.gameplay.guild.boss;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.JsonHelper;

/**
 * @author adex720
 */
public record GuildBossType(String name,
                            int id,
                            int color,
                            int hp,
                            GuildBossReward reward) {

    public GuildBoss createBoss() {
        return new GuildBoss(name, id, color, hp, reward);
    }

    public static GuildBossType fromJson(JsonObject json, int id) {
        String name = JsonHelper.getString(json, "name");
        int color = JsonHelper.getInt(json, "color");
        int hp = JsonHelper.getInt(json, "hp");

        JsonObject rewardJson = JsonHelper.getJsonObject(json, "reward");
        GuildBossReward reward = GuildBossReward.fromJson(rewardJson);

        return new GuildBossType(name, id, color, hp, reward);
    }
}
