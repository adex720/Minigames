package io.github.adex720.minigames.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.MinigameCommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

public abstract class MinigameType<M> {

    protected final MinigamesBot bot;
    protected final MinigameTypeManager typeManager;

    public final String name;
    public final String description;

    public final boolean requiresParty;
    public final int minPartySize;

    private MinigameCommand command;

    protected MinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, String description, boolean requiresParty, int minPartySize) {
        this.bot = bot;
        this.typeManager = typeManager;
        this.name = name;
        this.description = description;
        this.requiresParty = requiresParty;
        this.minPartySize = minPartySize;
    }

    public abstract M create(SlashCommandEvent event, CommandInfo ci);

    public abstract Subcommand createPlayCommand();

    public abstract Set<Subcommand> getSubcommands();

    public void initCommand(){
        command = new MinigameCommand(bot, name, description);
        getSubcommands().forEach(command::addSubcommand);
    }

    public MinigameCommand getCommand() {
        return command;
    }
}