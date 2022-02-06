package io.github.adex720.minigames.command;

import io.github.adex720.minigames.profile.Profile;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.entities.User;

public class CommandInfo {

    private final CalculableValue<Boolean> isInParty;
    private final CalculableValue<Party> party;

    private final CalculableValue<Boolean> hasProfile;
    private final CalculableValue<Profile> profile;

    private final CalculableValue<User> author;

    public CommandInfo(CalculableValue<Boolean> isInParty, CalculableValue<Party> party, CalculableValue<Boolean> hasProfile, CalculableValue<Profile> profile, CalculableValue<User> author) {
        this.isInParty = isInParty;
        this.party = party;
        this.hasProfile = hasProfile;
        this.profile = profile;
        this.author = author;
    }

    public boolean isInParty() {
        return isInParty.calculate();
    }

    public Party party() {
        return party.calculate();
    }

    public boolean hasProfile() {
        return hasProfile.calculate();
    }

    public Profile profile() {
        return profile.calculate();
    }

    public User author() {
        return author.calculate();
    }

    public long authorId() {
        return author.calculate().getIdLong();
    }

    public String authorMention() {
        return "<@!" + author.calculate().getId() + ">";
    }

    @FunctionalInterface
    public interface CalculableValue<T> {
        T calculate();
    }

}
