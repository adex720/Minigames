package io.github.adex720.minigames.gameplay.profile.stat;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Value;

import java.util.HashMap;
import java.util.Map;

public class StatList {

    private final HashMap<String, Value<Integer>> statsByName;
    private final HashMap<Integer, Value<Integer>> statsById;

    public StatList(MinigamesBot bot) {
        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Integer> value = new Value<>(0);

            statsByName.put(stat.name(), value);
            statsById.put(stat.id(), value);
        }
    }

    public StatList(MinigamesBot bot, JsonObject json) {
        statsByName = new HashMap<>();
        statsById = new HashMap<>();

        for (Stat stat : bot.getStatManager().getAll()) {
            Value<Integer> value = new Value<>(JsonHelper.getInt(json, Integer.toString(stat.id())));

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

    public void increaseStat(String stat) {
        statsByName.get(stat).value++; // TODO: investigate error: "Cannot read field "value" because "<local2>" is null"
    }

    public void increaseStat(String stat, int amount) {
        statsByName.get(stat).value += amount;
    }

    public void increaseStat(int stat) {
        statsById.get(stat).value++;
    }

    public void increaseStat(int stat, int amount) {
        statsById.get(stat).value += amount;
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        for (Map.Entry<Integer, Value<Integer>> entry : statsById.entrySet()) {
            json.addProperty(Integer.toString(entry.getKey()), entry.getValue().value);
        }

        return json;
    }

}
