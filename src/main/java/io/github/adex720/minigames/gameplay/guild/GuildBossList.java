package io.github.adex720.minigames.gameplay.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;

import java.util.ArrayList;

/**
 * @author adex720
 */
public class GuildBossList {

    private final MinigamesBot bot;

    private final ArrayList<GuildBossType> BOSSES;

    public GuildBossList(MinigamesBot bot) {
        this.bot = bot;

        BOSSES = new ArrayList<>();
        loadBosses(bot.getResourceJson("guild_bosses").getAsJsonArray());
    }

    private void loadBosses(JsonArray json) {
        int count = 0;
        for (JsonElement jsonElement : json) {
            JsonObject bossJson = jsonElement.getAsJsonObject();
            BOSSES.add(GuildBossType.fromJson(bossJson, count));
            count++;
        }
    }

    public GuildBoss get(int index) {
        return BOSSES.get(index).createBoss();
    }

}
