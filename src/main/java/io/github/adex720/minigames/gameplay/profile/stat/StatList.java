package io.github.adex720.minigames.gameplay.profile.stat;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Value;

import javax.annotation.CheckReturnValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages stats for one user.
 *
 * @author adex720
 */
public class StatList {

    private final MinigamesBot bot;

    private final HashMap<String, Value<Integer>> statsByName;
    private final HashMap<Integer, Value<Integer>> statsById;

    public StatList(MinigamesBot bot) {
        this.bot = bot;

        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Integer> value = new Value<>(0);

            statsByName.put(stat.name(), value);
            statsById.put(stat.id(), value);
        }
    }

    public StatList(MinigamesBot bot, JsonObject json) {
        this.bot = bot;

        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Integer> value = new Value<>(JsonHelper.getInt(json, Integer.toString(stat.id()), 0));

            statsByName.put(stat.name(), value);
            statsById.put(stat.id(), value);
        }
    }

    @CheckReturnValue
    public int getValue(String stat) {
        return statsByName.get(stat).value;
    }

    @CheckReturnValue
    public int getValue(int stat) {
        return statsById.get(stat).value;
    }

    public int increaseStat(Stat stat, Profile profile) {
        statsByName.get(stat.name()).value++;
        int newValue = statsById.get(stat.id()).value++;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
        return newValue;
    }

    public int increaseStat(Stat stat, int amount, Profile profile) {
        statsByName.get(stat.name()).value += amount;
        int newValue = statsById.get(stat.id()).value += amount;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
        return newValue;
    }

    public int increaseStat(String stat, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), profile);
    }

    public int increaseStat(String stat, int amount, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), amount, profile);
    }

    public int increaseStat(int stat, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), profile);
    }

    public int increaseStat(int stat, int amount, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), amount, profile);
    }

    public void setValue(Stat stat, int value, final Profile profile) {
        statsByName.get(stat.name()).value = value;
        statsById.get(stat.id()).value = value;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
    }

    public void setValue(int stat, int value, final Profile profile) {
        setValue(bot.getStatManager().get(stat), value, profile);
    }

    public void setValue(String stat, int value, final Profile profile) {
        setValue(bot.getStatManager().get(stat), value, profile);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        for (Map.Entry<Integer, Value<Integer>> entry : statsById.entrySet()) {
            int value = entry.getValue().value;
            if (value == 0) continue;
            json.addProperty(Integer.toString(entry.getKey()), value);
        }

        return json;
    }

    public int getAmount() {
        return statsById.size();
    }
}
