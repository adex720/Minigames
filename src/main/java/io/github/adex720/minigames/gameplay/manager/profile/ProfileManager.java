package io.github.adex720.minigames.gameplay.manager.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.Util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProfileManager extends IdCompoundSavableManager<Profile> {

    private final HashMap<Long, Profile> PROFILES;

    private final HashMap<Long, String> DELETION_CODES;

    public ProfileManager(MinigamesBot bot) {
        super(bot, "profile_manager");

        PROFILES = new HashMap<>();
        DELETION_CODES = new HashMap<>();

        load((JsonArray) bot.loadJson("profiles"));
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

    public void createProfile(long id) {
        Profile profile = Profile.create(bot, id);
        PROFILES.put(id, profile);
    }

    private void addProfile(Profile profile) {
        PROFILES.put(profile.getId(), profile);
    }

    public void deleteProfile(long id) {
        PROFILES.remove(id);
    }

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
}
