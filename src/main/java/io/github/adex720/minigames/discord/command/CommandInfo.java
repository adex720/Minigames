package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to access various variables throughout the execution of an interaction.
 * The values use  {@link CommandInfo.CalculableValue} interface to only calculate values once required.
 * Most of the methods also check if the value is already calculated and uses it if it is.
 *
 * @author adex720
 */
public class CommandInfo {

    @Nullable
    private Party calculatedParty;

    private final CalculableValue<Boolean> hasProfile;
    private final CalculableValue<Profile> profile;
    @Nullable
    private Profile calculatedProfile;

    private final User author;

    @Nullable
    private Minigame calculatedMinigame;

    private final MessageChannel channel;

    private final MinigamesBot bot;

    public CommandInfo(MessageChannel channel, User author, MinigamesBot bot) {
        this.hasProfile = () -> bot.getProfileManager().hasProfile(author.getIdLong());
        this.profile = () -> bot.getProfileManager().getProfile(author.getIdLong());
        this.author = author;
        this.channel = channel;
        this.bot = bot;

        calculatedParty = null;
        calculatedProfile = null;
        calculatedMinigame = null;
    }

    public static CommandInfo create(SlashCommandEvent event, MinigamesBot bot) {
        User user = event.getUser();
        return new CommandInfo(event.getChannel(), user, bot);
    }

    public static CommandInfo create(ButtonClickEvent event, MinigamesBot bot) {
        User user = event.getUser();
        return new CommandInfo(event.getChannel(), user, bot);
    }

    public static CommandInfo create(MessageReceivedEvent event, MinigamesBot bot) {
        User user = event.getAuthor();
        return new CommandInfo(event.getChannel(), user, bot);
    }


    public boolean isInParty() {
        if (calculatedParty != null) return true;
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
        if (calculatedMinigame != null) return true;
        return bot.getMinigameManager().hasMinigame(gameId());
    }

    public Minigame minigame() {
        if (calculatedMinigame == null) {
            calculatedMinigame = bot.getMinigameManager().getMinigame(gameId());
        }
        return calculatedMinigame;
    }

    public User author() {
        return author;
    }

    public long authorId() {
        return author.getIdLong();
    }

    public String getAuthorTag() {
        return author.getAsTag();
    }

    public String authorMention() {
        return "<@!" + author.getId() + ">";
    }

    public MessageChannel channel(){
        return channel;
    }

    public long channelId(){
        return channel.getIdLong();
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
