package io.github.adex720.minigames.gameplay.profile.booster;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.Arrays;

/**
 * List all boosters one player has in inventory.
 *
 * @author adex720
 * */
public class BoosterList {

    private final int[] boosters;

    public BoosterList() {
        boosters = new int[BoosterRarity.RARITIES_AMOUNT];
        Arrays.fill(boosters, 0);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        for (int i = 0; i < boosters.length; i++) {
            int count = boosters[i];

            if (count > 0) json.addProperty(Integer.toString(i), count);
        }

        return json;
    }

    public static BoosterList fromJson(JsonObject json) {
        if (json.size() == 0) return new BoosterList();

        BoosterList boosterList = new BoosterList();

        for (int i = 0; i < boosterList.boosters.length; i++) {
            String key = Integer.toString(i);

            if (json.has(key)) boosterList.boosters[i] = JsonHelper.getInt(json, key);
        }

        return boosterList;
    }

    public void add(int id) {
        boosters[id]++;
    }

    public void add(BoosterRarity type) {
        add(type.id);
    }

    public void add(int id, int amount) {
        boosters[id] += amount;
    }

    public void add(int id, BoosterRarity BoosterRarity) {
        add(id, BoosterRarity.id);
    }

    public void subtract(int id) {
        boosters[id]--;
    }

    public void subtract(BoosterRarity type) {
        subtract(type.id);
    }

    public void subtract(int id, int amount) {
        boosters[id] -= amount;
    }

    public void subtract(int id, BoosterRarity BoosterRarity) {
        subtract(id, BoosterRarity.id);
    }

    public int amount(int id) {
        return boosters[id];
    }

    public int amount(BoosterRarity BoosterRarity) {
        return amount(BoosterRarity.id);
    }

    public String toString(MinigamesBot bot) {
        if (isEmpty()) return "You don't have any boosters. You can get them from opening crates.";

        StringBuilder boostersString = new StringBuilder();

        boolean newLine = false;
        for (int id = 0; id < boosters.length; id++) {
            int amount = boosters[id];

            if (amount == 0) continue;

            if (newLine) boostersString.append('\n');
            newLine = true;

            BoosterRarity boosterRarity = BoosterRarity.get(id);
            boostersString.append(amount).append(' ').append(boosterRarity.getEmoteName(bot))
                    .append(" x").append(boosterRarity.multiplier).append(' ').append(boosterRarity.durationMinutes).append('m');
        }

        return boostersString.toString();
    }

    public boolean isEmpty() {
        for (int booster : boosters) {
            if (booster > 0) return false;
        }
        return true;
    }

}
