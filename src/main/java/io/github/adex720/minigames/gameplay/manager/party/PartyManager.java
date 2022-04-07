package io.github.adex720.minigames.gameplay.manager.party;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.gameplay.party.Party;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PartyManager extends IdCompoundSavableManager<Party> {

    private final HashMap<Long, Party> PARTIES;

    public PartyManager(MinigamesBot bot) {
        super(bot, "party_manager");

        PARTIES = new HashMap<>();
        load((JsonArray) bot.loadJson("parties"));
    }

    @Override
    public Party fromJson(JsonObject json) {
        return Party.fromJson(bot, json);
    }

    @Override
    public Set<Party> getValues() {
        return new HashSet<>(PARTIES.values());
    }

    public void clearInactiveParties() {
        long time = System.currentTimeMillis() - 1000 * 60 * 30;
        int amount = 0;

        Iterator<Party> iterator = PARTIES.values().iterator();
        while (iterator.hasNext()) {
            Party party = iterator.next();
            if (party.isInactive(time)) {
                iterator.remove();
                party.onDelete();
                amount++;
            }
        }

        bot.getLogger().info("Cleared {} inactive part{}.", amount, amount != 1 ? "ies" : "y");
    }

    public void createParty(long id) {
        Party party = new Party(bot, id);
        party.onCreate();
        PARTIES.put(id, party);
    }

    public void addParty(Party party) {
        PARTIES.put(party.getId(), party);
    }

    public void addPartyAndMembers(Party party) {
        addParty(party);
        party.updatePartyId();
    }

    public void removeParty(long id) {
        PARTIES.remove(id);
    }

    public boolean isPartyOwner(long id) {
        return PARTIES.containsKey(id);
    }

    public boolean isInParty(long id) {
        for (Party party : PARTIES.values()) {
            if (party.isInParty(id)) return true;
        }
        return false;
    }

    @Nullable
    public Party getParty(long id) {
        return PARTIES.get(id);
    }

    @Override
    public void load(JsonArray data) {
        for (JsonElement json : data) {
            addPartyAndMembers(fromJson((JsonObject) json));
        }
    }
}
