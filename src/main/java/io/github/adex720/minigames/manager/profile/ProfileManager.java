package io.github.adex720.minigames.manager.profile;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.profile.Profile;
import io.github.adex720.minigames.manager.IdCompoundSavableManager;

import java.util.Set;

public class ProfileManager extends IdCompoundSavableManager<Profile> {

    public ProfileManager(MinigamesBot bot) {
        super(bot, "profile_manager");
    }

    @Override
    public Profile fromJson(JsonObject json) {
        return null;
    }

    @Override
    public Set<Profile> getValues() {
        return null;
    }
}
