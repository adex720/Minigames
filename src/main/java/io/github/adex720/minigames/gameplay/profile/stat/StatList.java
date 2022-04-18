package io.github.adex720.minigames.gameplay.profile.stat;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages stats for one user.
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

    public int getValue(String stat) {
        return statsByName.get(stat).value;
    }

    public int getValue(int stat) {
        return statsById.get(stat).value;
    }

    public void increaseStat(Stat stat) {
        statsByName.get(stat.name()).value++;
        statsById.get(stat.id()).value++;
    }

    public void increaseStat(Stat stat, int amount) {
        statsByName.get(stat.name()).value += amount;
        statsById.get(stat.id()).value += amount;
    }

    public void increaseStat(String stat) {
        increaseStat(bot.getStatManager().get(stat));
    }

    public void increaseStat(String stat, int amount) {
        increaseStat(bot.getStatManager().get(stat), amount);
    }

    public void increaseStat(int stat) {
        increaseStat(bot.getStatManager().get(stat));
    }

    public void increaseStat(int stat, int amount) {
        increaseStat(bot.getStatManager().get(stat), amount);
    }

    public void setValue(Stat stat, int value) {
        statsByName.get(stat.name()).value = value;
        statsById.get(stat.id()).value = value;
    }

    public void setValue(int stat, int value) {
        setValue(bot.getStatManager().get(stat), value);
    }

    public void setValue(String stat, int value) {
        setValue(bot.getStatManager().get(stat), value);
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
