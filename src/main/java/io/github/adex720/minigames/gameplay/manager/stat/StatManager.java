package io.github.adex720.minigames.gameplay.manager.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages different stats and has methods for interacting with leaderboards.
 *
 * @author adex720
 */
public class StatManager extends Manager {

    private final HashMap<String, Stat> STATS_BY_NAME;
    private final HashMap<Integer, Stat> STATS_BY_ID;
    private final ArrayList<Stat> LEADERBOARD_STATS;

    private final HashMap<Integer, Leaderboard> LEADERBOARDS;

    public StatManager(MinigamesBot bot) {
        super(bot, "stat-manager");
        STATS_BY_NAME = new HashMap<>();
        STATS_BY_ID = new HashMap<>();
        LEADERBOARD_STATS = new ArrayList<>();

        LEADERBOARDS = new HashMap<>();
        load(bot);
    }

    public void initLeaderboards() {
        for (Stat stat : LEADERBOARD_STATS) {
            final int id = stat.id();
            LEADERBOARDS.put(id, new Leaderboard(bot.getProfileManager().getValues(), id));
        }
    }

    private void load(MinigamesBot bot) {
        JsonArray statsJson = bot.getResourceJson("stats").getAsJsonArray();

        for (JsonElement statJson : statsJson) {
            Stat stat = Stat.fromJson(statJson.getAsJsonObject());
            STATS_BY_NAME.put(stat.name(), stat);
            STATS_BY_ID.put(stat.id(), stat);

            if (stat.onLeaderboard()) LEADERBOARD_STATS.add(stat);
        }
    }

    public Stat get(String name) {
        return STATS_BY_NAME.get(name);
    }

    public Stat get(int id) {
        return STATS_BY_ID.get(id);
    }

    public Set<Stat> getAll() {
        return new HashSet<>(STATS_BY_NAME.values());
    }

    public ArrayList<Stat> getLeaderboardStats() {
        return LEADERBOARD_STATS;
    }

    public Leaderboard getLeaderboard(int statId) {
        return LEADERBOARDS.get(statId);
    }

    public void addToLeaderboards(Profile profile) {
        for (Leaderboard leaderboard : LEADERBOARDS.values()) {
            leaderboard.add(profile);
        }
    }

    public void removeFromLeaderboards(Profile profile) {
        for (Leaderboard leaderboard : LEADERBOARDS.values()) {
            leaderboard.remove(profile);
        }
    }

}
