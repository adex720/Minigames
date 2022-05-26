package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;

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

    private final CalculableValue<Boolean> isInGuild;
    private final CalculableValue<Guild> guild;
    private Boolean calculatedIsInGuild;
    private Guild calculatedGuild;

    private final User author;

    @Nullable
    private Minigame calculatedMinigame;

    private final MessageChannel channel;

    private final MinigamesBot bot;

    @Nullable
    private final CalculableValue<String[]> args;
    private String[] calculatedArgs;

    public CommandInfo(MessageChannel channel, User author, MinigamesBot bot, @Nullable CalculableValue<String[]> args) {
        this.hasProfile = () -> bot.getProfileManager().hasProfile(author.getIdLong());
        this.profile = () -> bot.getProfileManager().getProfile(author.getIdLong());

        this.isInGuild = () -> bot.getGuildManager().isInGuild(author.getIdLong());
        this.guild = () -> bot.getGuildManager().getGuild(author.getIdLong());

        this.author = author;
        this.channel = channel;

        this.bot = bot;
        this.args = args;

        calculatedParty = null;
        calculatedProfile = null;
        calculatedGuild = null;
        this.calculatedIsInGuild = null;
        calculatedMinigame = null;
        calculatedArgs = null;
    }

    public static CommandInfo create(SlashCommandInteractionEvent event, MinigamesBot bot) {
        User user = event.getUser();
        return new CommandInfo(event.getChannel(), user, bot, null);
    }

    public static CommandInfo create(ButtonInteractionEvent event, MinigamesBot bot) {
        User user = event.getUser();

        String buttonId = event.getButton().getId();
        CalculableValue<String[]> args = buttonId != null ? () -> buttonId.split("-") : null;

        return new CommandInfo(event.getChannel(), user, bot, args);
    }

    public static CommandInfo create(MessageReceivedEvent event, MinigamesBot bot) {
        User user = event.getAuthor();
        return new CommandInfo(event.getChannel(), user, bot, () -> event.getMessage().getContentRaw().split(" "));
    }

    @CheckReturnValue
    public boolean isInParty() {
        if (calculatedParty != null) return true;
        return profile().isInParty();
    }

    @CheckReturnValue
    public Party party() {
        if (calculatedParty == null) {
            calculatedParty = bot.getPartyManager().getParty(profile().getPartyId());
        }

        return calculatedParty;
    }

    @CheckReturnValue
    public boolean isInGuild() {
        if (calculatedGuild != null) return true;
        if (calculatedIsInGuild == null) calculatedIsInGuild = isInGuild.calculate();

        return calculatedIsInGuild;
    }

    @CheckReturnValue
    public Guild guild() {
        if (calculatedGuild == null) {
            calculatedGuild = bot.getGuildManager().getGuild(authorId());
        }

        return calculatedGuild;
    }

    @CheckReturnValue
    public long gameId() {
        return isInParty() ? profile().getPartyId() : profile().getId();
    }

    @CheckReturnValue
    public boolean hasProfile() {
        return hasProfile.calculate();
    }

    @CheckReturnValue
    public Profile profile() {
        if (calculatedProfile == null) {
            calculatedProfile = profile.calculate();

            if (calculatedProfile == null) return null;
            calculatedProfile.setTag(author.getAsTag()); // Update tag
        }

        return calculatedProfile;
    }

    @CheckReturnValue
    public boolean hasMinigame() {
        if (calculatedMinigame != null) return true;
        return bot.getMinigameManager().hasMinigame(gameId());
    }

    @CheckReturnValue
    public Minigame minigame() {
        if (calculatedMinigame == null) {
            calculatedMinigame = bot.getMinigameManager().getMinigame(gameId());
        }
        return calculatedMinigame;
    }

    @CheckReturnValue
    public User author() {
        return author;
    }

    @CheckReturnValue
    public long authorId() {
        return author.getIdLong();
    }

    @CheckReturnValue
    public String authorIdString() {
        return author.getId();
    }

    @CheckReturnValue
    public String getAuthorTag() {
        return author.getAsTag();
    }

    @CheckReturnValue
    public String authorMention() {
        return "<@!" + author.getId() + ">";
    }

    @CheckReturnValue
    public MessageChannel channel() {
        return channel;
    }

    @CheckReturnValue
    public long channelId() {
        return channel.getIdLong();
    }

    @CheckReturnValue
    public MinigamesBot bot() {
        return bot;
    }

    @CheckReturnValue
    public String[] args() {
        if (calculatedArgs == null && args != null) {
            calculatedArgs = args.calculate();
        }

        return calculatedArgs;
    }

    @CheckReturnValue
    public String getArgumentOrDefault(int id, String defaultValue) {
        String[] args = args();
        if (args == null) return defaultValue;
        if (args.length <= id) return defaultValue;

        return args[id];
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
