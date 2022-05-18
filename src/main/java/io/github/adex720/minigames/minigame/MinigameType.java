package io.github.adex720.minigames.minigame;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.MinigameCommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Stores information common with each of the minigames of same type.
 *
 * @author adex720
 */
public abstract class MinigameType<M extends Minigame> {

    protected final MinigamesBot bot;
    protected final MinigameTypeManager typeManager;

    public final String name;
    public final String description;
    public final String rules;
    public final String tips;

    public final int color;

    public final boolean requiresParty;
    public final int minPartySize;

    private MinigameCommand command;

    protected MinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, boolean requiresParty, int minPartySize) {
        this.bot = bot;
        this.typeManager = typeManager;
        this.name = name;

        this.requiresParty = requiresParty;
        this.minPartySize = minPartySize;

        JsonObject json = bot.getResourceJson("minigames").getAsJsonObject();

        JsonObject minigameJson = JsonHelper.getJsonObject(json, name);
        this.description = JsonHelper.getString(minigameJson, "description");
        this.tips = JsonHelper.getString(minigameJson, "tips");
        this.rules = JsonHelper.getString(minigameJson, "rules");
        this.color = Integer.parseInt(JsonHelper.getString(minigameJson, "color"), 16);
    }

    @Nullable
    public abstract M create(SlashCommandInteractionEvent event, CommandInfo ci);

    @Nullable
    public abstract M create(ButtonInteractionEvent event, CommandInfo ci);

    public abstract M fromJson(JsonObject json);

    public void createPlayCommand() {
        bot.getCommandManager().parentCommandPlay.createSubcommand(this);
    }

    /**
     * @return Each command required to interact with the minigame
     */
    public abstract Set<Subcommand> getSubcommands();

    public void initCommand() {
        command = new MinigameCommand(bot, name, "Performs actions in a game of " + name);
        getSubcommands().forEach(command::addSubcommand);
        //bot.getCommandManager().addCommand(command);
    }

    /**
     * @return the parent command for interacting with the minigame.
     */
    public MinigameCommand getCommand() {
        return command;
    }

    public boolean canStart(CommandInfo commandInfo) {
        if (!requiresParty) return true; // no requirements

        if (!commandInfo.isInParty()) return false; // not in party while but it's required

        return commandInfo.party().size() >= minPartySize;
    }

    /**
     * If the initialization of minigame ends this will be sent as reason.
     * If the initialization of the minigame can't fail it doesn't matter what is returned.
     */
    public String getReplyForInvalidStartState() {
        return "";
    }

    /**
     * Returns the name with dashes being replaced by spaces.
     */
    public String getNameWithSpaces() {
        return name.replaceAll("-", " ");
    }

    /**
     * @return id of the state the game should be started with.
     */
    public int getState(long gameId) {
        return getDefaultState();
    }

    /**
     * @return id of the default state for this minigame.
     */
    public int getDefaultState() {
        return 1;
    }
}
