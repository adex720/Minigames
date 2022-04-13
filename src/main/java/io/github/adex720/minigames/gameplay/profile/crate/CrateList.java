package io.github.adex720.minigames.gameplay.profile.crate;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.JsonHelper;

/**
 * Lists all unopened crates a player has.
 * */
public class CrateList {

    private final int[] crates;

    public CrateList() {
        crates = new int[CrateType.TYPES_AMOUNT];
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        for (int i = 0; i < crates.length; i++) {
            int count = crates[i];

            if (count > 0) json.addProperty(Integer.toString(i), count);
        }

        return json;
    }

    public static CrateList fromJson(JsonObject json) {
        if (json.size() == 0) return new CrateList();

        CrateList crateList = new CrateList();

        for (int i = 0; i < crateList.crates.length; i++) {
            String key = Integer.toString(i);

            if (json.has(key)) crateList.crates[i] = JsonHelper.getInt(json, key);
        }

        return crateList;
    }

    public void add(int id) {
        crates[id]++;
    }

    public void add(CrateType type) {
        add(type.id);
    }

    public void add(int id, int amount) {
        crates[id] += amount;
    }

    public void add(int id, CrateType crateType) {
        add(id, crateType.id);
    }

    public void subtract(int id) {
        crates[id]--;
    }

    public void subtract(CrateType type) {
        subtract(type.id);
    }

    public void subtract(int id, int amount) {
        crates[id] -= amount;
    }

    public void subtract(int id, CrateType crateType) {
        subtract(id, crateType.id);
    }

    public int amount(int id) {
        return crates[id];
    }

    public int amount(CrateType crateType) {
        return amount(crateType.id);
    }

    public String toString(MinigamesBot bot) {
        if (isEmpty()) return "You don't have any crates. You can get them from kits or playing minigames.";

        StringBuilder cratesString = new StringBuilder();

        boolean newLine = false;
        for (int id = 0; id < crates.length; id++) {
            int amount = crates[id];

            if (amount == 0) continue;

            if (newLine) cratesString.append('\n');
            newLine = true;

            CrateType crateRarity = CrateType.get(id);
            cratesString.append(amount).append(' ').append(crateRarity.getEmoteName(bot))
                    .append(' ').append(crateRarity.name).append(" crates");
        }

        return cratesString.toString();
    }

    public boolean isEmpty() {
        for (int crate : crates) {
            if (crate > 0) return false;
        }
        return true;
    }

    public int size() {
        int size = 0;
        for (int crate : crates) {
            size += crate;
        }
        return size;
    }

    public int[] values() {
        return crates;
    }

}
