package io.github.adex720.minigames.gameplay.manager.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.badge.Badge;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.*;

/**
 * Manages {@link Badge}
 *
 * @author adex720
 */
public class BadgeManager extends Manager {

    private final SortedMap<Integer, Badge> BADGES;
    private final HashMap<String, Badge> BADGES_BY_NAME;
    private int BADGES_AMOUNT;

    public BadgeManager(MinigamesBot bot) {
        super(bot, "badge-manager");

        BADGES = new TreeMap<>((o1, o2) -> o2 - o1);
        BADGES_BY_NAME = new HashMap<>();
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
        BADGES_BY_NAME.put(badge.name(), badge);
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

    /**
     * @param name name of badge
     */
    public Badge getBadge(String name) {
        return BADGES_BY_NAME.get(name);
    }

    public void addBadge(Badge badge, Profile profile) {
        profile.addBadge(badge.id());
    }

    public void addBadge(Badge badge, int userId) {
        addBadge(badge, bot.getProfileManager().getProfile(userId));
    }

    public void addBadgeForEveryone(Badge badge) {
        bot.getProfileManager().getValues().forEach(profile -> addBadge(badge, profile));
    }

    public int[] getBadgesForNewUsers() {
        return new int[]{1};
    }
}
