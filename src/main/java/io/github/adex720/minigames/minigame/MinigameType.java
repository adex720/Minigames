package io.github.adex720.minigames.minigame;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.MinigameCommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

public abstract class MinigameType<M extends Minigame> {

    protected final MinigamesBot bot;
    protected final MinigameTypeManager typeManager;

    public final String name;
    public final String description;

    public final boolean requiresParty;
    public final int minPartySize;

    private MinigameCommand command;

    protected MinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, boolean requiresParty, int minPartySize) {
        this.bot = bot;
        this.typeManager = typeManager;
        this.name = name;
        this.description = getDescription();
        this.requiresParty = requiresParty;
        this.minPartySize = minPartySize;
    }

    protected String getDescription() {
        JsonObject json = bot.getResourceJson("minigames").getAsJsonObject();

        JsonObject minigameJson = JsonHelper.getJsonObject(json, name);
        return JsonHelper.getString(minigameJson, "description");
    }

    public abstract M create(SlashCommandEvent event, CommandInfo ci);

    public abstract M create(ButtonClickEvent event, CommandInfo ci);

    public abstract M fromJson(JsonObject json);

    public void createPlayCommand() {
        bot.getCommandManager().parentCommandPlay.createSubcommand(this);
    }

    public abstract Set<Subcommand> getSubcommands();

    public void initCommand() {
        command = new MinigameCommand(bot, name, "Performs actions in a game of " + name);
        getSubcommands().forEach(command::addSubcommand);
        //bot.getCommandManager().addCommand(command);
    }

    public MinigameCommand getCommand() {
        return command;
    }
}
