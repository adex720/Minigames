package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * This class is used to access various variables throughout the execution of an interaction.
 * The values use  {@link CommandInfo.CalculableValue} interface to only calculate values once required.
 * Most of the methods also check if the value is already calculated and uses it if it is.
 */
public class CommandInfo {
    private Party calculatedParty;

    private final CalculableValue<Boolean> hasProfile;
    private final CalculableValue<Profile> profile;
    private Profile calculatedProfile;

    private final CalculableValue<User> author;
    private User calculatedAuthor;

    private Minigame calculatedMinigame;

    private final MinigamesBot bot;

    public CommandInfo(CalculableValue<Boolean> hasProfile, CalculableValue<Profile> profile, CalculableValue<User> author, MinigamesBot bot) {
        this.hasProfile = hasProfile;
        this.profile = profile;
        this.author = author;
        this.bot = bot;

        calculatedParty = null;
        calculatedProfile = null;
        calculatedAuthor = null;
        calculatedMinigame = null;
    }

    public static CommandInfo create(SlashCommandEvent event, MinigamesBot bot) {
        Member member = event.getMember(); // member is null when command is from dms. (Commands from dms are ignored)
        return new CommandInfo(
                () -> bot.getProfileManager().hasProfile(member.getIdLong()),
                () -> bot.getProfileManager().getProfile(member.getIdLong()),
                member::getUser, bot);
    }

    public static CommandInfo create(ButtonClickEvent event, MinigamesBot bot) {
        Member member = event.getMember();
        return new CommandInfo(
                () -> bot.getProfileManager().hasProfile(member.getIdLong()),
                () -> bot.getProfileManager().getProfile(member.getIdLong()),
                member::getUser, bot);
    }

    public boolean isInParty() {
        return profile().isInParty();
    }

    public Party party() {
        if (calculatedParty == null) {
            calculatedParty = bot.getPartyManager().getParty(profile().getPartyId());
        }

        return calculatedParty;
    }

    public long gameId() {
        return isInParty() ? profile().getPartyId() : profile().getId();
    }

    public boolean hasProfile() {
        return hasProfile.calculate();
    }

    public Profile profile() {
        if (calculatedProfile == null) {
            calculatedProfile = profile.calculate();
        }

        return profile.calculate();
    }

    public boolean hasMinigame() {
        return bot.getMinigameManager().hasMinigame(gameId());
    }

    public Minigame minigame() {
        if (calculatedMinigame == null) {
            calculatedMinigame = bot.getMinigameManager().getMinigame(gameId());
        }
        return calculatedMinigame;
    }

    public User author() {
        if (calculatedAuthor == null) {
            calculatedAuthor = author.calculate();
        }
        return calculatedAuthor;
    }

    public long authorId() {
        if (calculatedAuthor == null) {
            calculatedAuthor = author.calculate();
        }
        return calculatedAuthor.getIdLong();
    }

    public String getAuthorTag() {
        if (calculatedAuthor == null) {
            calculatedAuthor = author.calculate();
        }
        return calculatedAuthor.getAsTag();
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

    @Override
    public String toString() {
        return "Author id: " + authorId() + " is in party: " + isInParty() + " active minigame: " + (hasMinigame() ? minigame().getType().name : "none");
    }
}
