package io.github.adex720.minigames.manager.profile;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.profile.Profile;

import java.util.HashMap;
import java.util.Set;

public class ProfileManager extends IdCompoundSavableManager<Profile> {

    private final HashMap<Long, Profile> PROFILES;

    public ProfileManager(MinigamesBot bot) {
        super(bot, "profile_manager");

        PROFILES = new HashMap<>();
        createProfile(560815341140181034L);
        createProfile(864318052812062731L);
    }

    @Override
    public Profile fromJson(JsonObject json) {
        return null;
    }

    @Override
    public Set<Profile> getValues() {
        return null;
    }

    public boolean hasProfile(long userId) {
        return PROFILES.containsKey(userId);
    }

    public Profile getProfile(long userId) {
        return PROFILES.get(userId);
    }

    public Profile createProfile(long id) {
        Profile profile = new Profile(id);
        PROFILES.put(id, profile);
        return profile;
    }
}
