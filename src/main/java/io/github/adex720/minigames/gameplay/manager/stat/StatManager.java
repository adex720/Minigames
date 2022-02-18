package io.github.adex720.minigames.gameplay.manager.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;

import java.util.*;

public class StatManager extends Manager {

    private final HashMap<String, Stat> STATS_BY_NAME;
    private final HashMap<Integer, Stat> STATS_BY_ID;
    private final ArrayList<Stat> LEADERBOARD_STATS;

    private final HashMap<Integer, TreeSet<Profile>> LEADERBOARDS;

    public StatManager(MinigamesBot bot) {
        super(bot, "stat-manager");
        STATS_BY_NAME = new HashMap<>();
        STATS_BY_ID = new HashMap<>();
        LEADERBOARD_STATS = new ArrayList<>();

        LEADERBOARDS = new HashMap<>();
        load(bot);

        for (Stat stat : LEADERBOARD_STATS) {
            final int id = stat.id();
            LEADERBOARDS.put(stat.id(), new TreeSet<>(Comparator.comparing(profile -> profile.getValue(id))));
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

    public void updateLeaderboards() {
        for (Stat stat : LEADERBOARD_STATS) {
            if (!stat.onLeaderboard()) continue;
            updateLeaderboard(stat);
        }
    }

    public void updateLeaderboard(Stat stat) {
        int statId = stat.id();

        TreeSet<Profile> leaderboard = new TreeSet<>(Comparator.comparing(profile -> profile.getValue(statId)));
        leaderboard.addAll(bot.getProfileManager().getValues());

        LEADERBOARDS.put(statId, leaderboard);
    }

    @SuppressWarnings("unchecked")
    public TreeSet<Profile> getLeaderboard(int statId){
        return (TreeSet<Profile>) LEADERBOARDS.get(statId).clone();
    }

}
