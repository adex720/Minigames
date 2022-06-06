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

    private final HashMap<String, Value<Long>> statsByName;
    private final HashMap<Integer, Value<Long>> statsById;

    public StatList(MinigamesBot bot) {
        this.bot = bot;

        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Long> value = new Value<>(0L);

            statsByName.put(stat.name(), value);
            statsById.put(stat.id(), value);
        }
    }

    public StatList(MinigamesBot bot, JsonObject json) {
        this.bot = bot;

        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Long> value = new Value<>(JsonHelper.getLong(json, Long.toString(stat.id()), 0));

            statsByName.put(stat.name(), value);
            statsById.put(stat.id(), value);
        }
    }

    @CheckReturnValue
    public long getValue(String stat) {
        return statsByName.get(stat).value;
    }

    @CheckReturnValue
    public long getValue(int stat) {
        return statsById.get(stat).value;
    }

    public long increaseStat(Stat stat, Profile profile) {
        statsByName.get(stat.name()).value++;
        long newValue = statsById.get(stat.id()).value++;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
        return newValue;
    }

    public long increaseStat(Stat stat, long amount, Profile profile) {
        statsByName.get(stat.name()).value += amount;
        long newValue = statsById.get(stat.id()).value += amount;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
        return newValue;
    }

    public long increaseStat(String stat, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), profile);
    }

    public long increaseStat(String stat, long amount, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), amount, profile);
    }

    public long increaseStat(int stat, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), profile);
    }

    public long increaseStat(int stat, long amount, Profile profile) {
        return increaseStat(bot.getStatManager().get(stat), amount, profile);
    }

    public void setValue(Stat stat, long value, final Profile profile) {
        statsByName.get(stat.name()).value = value;
        statsById.get(stat.id()).value = value;

        if (stat.onLeaderboard()) {
            bot.getStatManager().getLeaderboard(stat.id()).update(profile);
        }
    }

    public void setValue(int stat, long value, final Profile profile) {
        setValue(bot.getStatManager().get(stat), value, profile);
    }

    public void setValue(String stat, long value, final Profile profile) {
        setValue(bot.getStatManager().get(stat), value, profile);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        for (Map.Entry<Integer, Value<Long>> entry : statsById.entrySet()) {
            long value = entry.getValue().value;
            if (value == 0) continue;
            json.addProperty(Long.toString(entry.getKey()), value);
        }

        return json;
    }

    public long getAmount() {
        return statsById.size();
    }
}
