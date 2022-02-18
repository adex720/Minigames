package io.github.adex720.minigames.gameplay.profile.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.Value;

import java.util.HashMap;
import java.util.Map;

public class StatList {

    private final HashMap<String, Value<Integer>> stats;

    public StatList() {
        stats = new HashMap<>();
    }

    public void increaseStat(String stat) {
        stats.get(stat).value++;
    }

    public void increaseStat(String stat, int amount) {
        stats.get(stat).value += amount;
    }

    public JsonArray asJson() {
        JsonArray jsonArray = new JsonArray();

        for (Map.Entry<String, Value<Integer>> entry : stats.entrySet()) {
            JsonObject json = new JsonObject();

            json.addProperty("stat", entry.getKey());
            json.addProperty("value", entry.getValue().value);

            jsonArray.add(json);
        }

        return jsonArray;
    }

}
