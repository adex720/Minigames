package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import net.dv8tion.jda.api.entities.User;

public class CommandInfo {

    private final CalculableValue<Boolean> isInParty;
    private final CalculableValue<Party> party;

    private final CalculableValue<Boolean> hasProfile;
    private final CalculableValue<Profile> profile;

    private final CalculableValue<User> author;
    private User authorUser;

    private final MinigamesBot bot;

    public CommandInfo(CalculableValue<Boolean> isInParty, CalculableValue<Party> party, CalculableValue<Boolean> hasProfile, CalculableValue<Profile> profile, CalculableValue<User> author, MinigamesBot bot) {
        this.isInParty = isInParty;
        this.party = party;
        this.hasProfile = hasProfile;
        this.profile = profile;
        this.author = author;
        this.bot = bot;
        authorUser = null;
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
        if (authorUser == null) {
            authorUser = author.calculate();
        }
        return authorUser;
    }

    public long authorId() {
        if (authorUser == null) {
            authorUser = author.calculate();
        }
        return authorUser.getIdLong();
    }

    public String getAuthorTag() {
        if (authorUser == null) {
            authorUser = author.calculate();
        }
        return authorUser.getAsTag();
    }

    public String authorMention() {
        return "<@!" + author.calculate().getId() + ">";
    }

    public MinigamesBot bot() {
        return bot;
    }

    @FunctionalInterface
    public interface CalculableValue<T> {
        T calculate();
    }

}
