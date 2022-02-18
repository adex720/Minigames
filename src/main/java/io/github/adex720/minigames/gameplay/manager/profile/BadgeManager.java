package io.github.adex720.minigames.gameplay.manager.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.Badge;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class BadgeManager extends Manager {

    private final SortedMap<Integer, Badge> BADGES;
    private int BADGES_AMOUNT;

    public BadgeManager(MinigamesBot bot) {
        super(bot, "badge-manager");

        BADGES = new TreeMap<>((o1, o2) -> o2 - o1);
        loadBadges();
    }

    private void loadBadges() {
        JsonArray badgesJson = bot.getResourceJson("badges").getAsJsonArray();

        for (JsonElement badge : badgesJson) {
            registerBadge(badge.getAsJsonObject());
        }
    }

    private void registerBadge(JsonObject json) {
        int id = JsonHelper.getInt(json, "id");
        String name = JsonHelper.getString(json, "name");
        String emoji = JsonHelper.getString(json, "emoji");

        registerBadge(new Badge(id, name, emoji));
    }

    public void registerBadge(Badge badge) {
        BADGES.put(badge.id(), badge);
        BADGES_AMOUNT++;
    }

    public Badge getBadge(int id) {
        return BADGES.get(id);
    }

    public ArrayList<Badge> getBadges(Set<Integer> ids) {
        ArrayList<Badge> badges = new ArrayList<>();
        badges.ensureCapacity(ids.size());
        int found = 0;
        int badgesObtained = ids.size();

        for (Badge badge : BADGES.values()) {
            if (ids.contains(badge.id())) {
                badges.add(badge);
                found++;
                if (found == badgesObtained) {
                    return badges;
                }
            }
        }
        return badges;
    }
}
