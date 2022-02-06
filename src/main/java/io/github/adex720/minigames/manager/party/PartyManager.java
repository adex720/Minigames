package io.github.adex720.minigames.manager.party;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.party.Party;
import io.github.adex720.minigames.manager.IdCompoundSavableManager;
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

        Iterator<Party> iterator = PARTIES.values().iterator();
        while (iterator.hasNext()) {
            Party party = iterator.next();
            if (party.isInactive(time)) {
                iterator.remove();
                party.onDelete();
            }
        }
    }

    public void createParty(long id){
        PARTIES.put(id, new Party(bot, id));
    }

    public void addParty(long id, Party party){
        PARTIES.put(id, party);
    }

    public boolean isPartyOwner(long id){
        return PARTIES.containsKey(id);
    }

    @Nullable
    public Party getParty(long id){
        return PARTIES.get(id);
    }

}
