package io.github.adex720.minigames.gameplay.profile;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;

public class Profile implements IdCompound, JsonSavable<Profile> {

    private final long userId;

    private boolean isInParty;
    private long partyId;

    public Profile(long userId) {
        this.userId = userId;
        isInParty = false;
        partyId = userId;
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public JsonObject getAsJson() {
        return null;
    }

    public static Profile fromJson(JsonObject json) {
        return new Profile(1L);
    }

    public boolean isInParty() {
        return isInParty;
    }

    public long getPartyId() {
        return partyId;
    }

    public void partyJoined(long partyId){
        isInParty = true;
        this.partyId = partyId;
    }

    public void partyLeft(){
        isInParty = false;
    }
}
