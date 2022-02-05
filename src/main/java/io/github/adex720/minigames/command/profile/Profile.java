package io.github.adex720.minigames.command.profile;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;

public class Profile implements IdCompound, JsonSavable<Profile> {

    private final long userId;

    private boolean isInParty;
    private long partyId;

    public Profile(long userId) {
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public JsonObject getAsJson() {
        return null;
    }

    public static Profile fromJson(JsonObject json){

    }
}
