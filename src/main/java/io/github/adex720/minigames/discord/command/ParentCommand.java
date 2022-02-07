package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

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

    public void addSubcommand(Subcommand subcommand) {
        SUB_COMMANDS.add(subcommand);
        getCommandData().addSubcommands(subcommand.getSubcommandData());
    }

}
