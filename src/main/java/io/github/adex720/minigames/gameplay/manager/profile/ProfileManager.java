package io.github.adex720.minigames.gameplay.manager.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages {@link Profile}s.
 *
 * @author adex720
 */
public class ProfileManager extends IdCompoundSavableManager<Profile> {

    private final HashMap<Long, Profile> PROFILES;

    private final HashMap<Long, String> DELETION_CODES;

    public ProfileManager(MinigamesBot bot) {
        super(bot, "profile_manager");

        PROFILES = new HashMap<>();
        DELETION_CODES = new HashMap<>();

        load(bot.loadJson("profiles").getAsJsonArray());
    }

    @Override
    public Profile fromJson(JsonObject json) {
        return Profile.fromJson(bot, json);
    }

    @Override
    public Set<Profile> getValues() {
        return new HashSet<>(PROFILES.values());
    }

    public boolean hasProfile(long userId) {
        return PROFILES.containsKey(userId);
    }

    public Profile getProfile(long userId) {
        return PROFILES.get(userId);
    }

    public void createProfile(long id, String tag) {
        Profile profile = Profile.create(bot, id, tag);
        PROFILES.put(id, profile);

        BadgeManager badgeManager = bot.getBadgeManager();
        for (int badgeId : badgeManager.getBadgesForNewUsers()) {
            profile.addBadge(badgeId);
        }
    }

    private void addProfile(Profile profile) {
        PROFILES.put(profile.getId(), profile);

        bot.getStatManager().addToLeaderboards(profile);
    }

    /**
     * @param event event to reply minigame results.
     */
    public void deleteProfile(SlashCommandInteractionEvent event, long id) {
        Profile profile = PROFILES.remove(id);

        profile.onDelete(event);
        bot.getStatManager().removeFromLeaderboards(profile);
    }

    /**
     * Generates a verification code to prevent accidental profile deletions.
     * The code expires after 10 seconds.
     *
     * @param id Id of the user whose profile is getting deleted
     * @return The verification code
     */
    public String generateDeletionCode(long id) {
        char[] letters = new char[5];

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            letters[i] = (char) ('A' + random.nextInt(26));
        }

        String code = new String(letters);
        DELETION_CODES.put(id, code);
        Util.schedule(() -> DELETION_CODES.remove(id, code), 10000);
        return code;
    }

    public boolean doesDeletionCodeMatch(long id, String code) {
        String correct = DELETION_CODES.get(id);
        if (correct == null) return false;

        return correct.equals(code.toUpperCase(Locale.ROOT));
    }

    @Override
    public void load(JsonArray data) {
        for (JsonElement json : data) {
            addProfile(fromJson((JsonObject) json));
        }
    }

    void ban(long userId) {
        Profile profile = PROFILES.remove(userId);
        if (profile == null) return; // Prevents given id of creating a profile
        profile.ban();
        PROFILES.put(-profile.getId(), profile); // Profiles are saved, but unbound.
    }

    void unban(long userId) {
        Profile profile = PROFILES.remove(-userId);
        if (profile == null) return;
        profile.unban();
        PROFILES.put(userId, profile);
    }

    public int getProfilesAmount() {
        return PROFILES.size();
    }
}
