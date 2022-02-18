package io.github.adex720.minigames.gameplay.manager.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;

import java.util.HashMap;

public class StatManager extends Manager {

    private final HashMap<String, Stat> STATS_BY_NAME;
    private final HashMap<Integer, Stat> STATS_BY_ID;

    public StatManager(MinigamesBot bot) {
        super(bot, "stat-manager");
        STATS_BY_NAME = new HashMap<>();
        STATS_BY_ID = new HashMap<>();

        load(bot);
    }

    private void load(MinigamesBot bot) {
        JsonArray statsJson = bot.getResourceJson("stats").getAsJsonArray();

        for (JsonElement statJson : statsJson) {
            Stat stat = Stat.fromJson(statJson.getAsJsonObject());
            STATS_BY_NAME.put(stat.name(), stat);
            STATS_BY_ID.put(stat.id(), stat);
        }
    }

    public Stat get(String name) {
        return STATS_BY_NAME.get(name);
    }

    public Stat get(int id) {
        return STATS_BY_ID.get(id);
    }

}
