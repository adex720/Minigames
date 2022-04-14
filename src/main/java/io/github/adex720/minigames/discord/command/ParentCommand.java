package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Once this command is run all of its subCommands are looped through and the correct one is executed.
 * If the command shown at Discord is "/foo bah", foo is the parent command and bah is a subcommand
 * All {@link Subcommand}s must be registered with {@link ParentCommand#addSubcommand(Subcommand)}.
 * */
public abstract class ParentCommand extends Command {

    protected final Set<Subcommand> SUB_COMMANDS;

    public ParentCommand(MinigamesBot bot, String name, String description, CommandCategory category) {
        super(bot, name, description, category);
        SUB_COMMANDS = new HashSet<>();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        String subCommandName = event.getSubcommandName();

        for (Subcommand subCommand : SUB_COMMANDS) {
            if (subCommandName.equals(subCommand.getMainName())) {
                return subCommand.execute(event, ci);
            }
        }

        return true;
    }

    /**
     * Adds {@param subcommand} to both {@link ParentCommand#SUB_COMMANDS} and the command data.
     * */
    public void addSubcommand(Subcommand subcommand) {
        SUB_COMMANDS.add(subcommand);
        commandData.addSubcommands(subcommand.getSubcommandData());
    }

    /**
     * This only applies to the parent command.
     * Each subcommand will be included on help if not made different.
     * <p>
     * This will make it so /party won't be shown at help but all of its sub commands will.
     * */
    @Override
    public boolean shouldBeInHelp(CommandCategory category) {
        return false;
    }

    @Override
    public final boolean isParentCommand() {
        return true;
    }
}
