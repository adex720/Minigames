package io.github.adex720.minigames.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.miscellaneous.CommandPing;
import io.github.adex720.minigames.manager.Manager;
import net.dv8tion.jda.api.JDA;

import java.util.HashSet;
import java.util.Set;

public class CommandManager extends Manager {

    private static final boolean SHOULD_RELOAD_COMMANDS = true;

    public final Set<Command> COMMANDS;

    public CommandManager(MinigamesBot bot) {
        super(bot, "command_manager");
        COMMANDS = new HashSet<>();
        initCommands(bot);
    }

    private void initCommands(MinigamesBot bot) {
        COMMANDS.add(new CommandPing(bot));
    }

    public void registerCommands(JDA jda) {
        if (SHOULD_RELOAD_COMMANDS) {
            for (Command command : COMMANDS) {
                jda.upsertCommand(command.createCommandData()).queue();
            }

            bot.getLogger().info("Registered all commands");
        }
    }


}
