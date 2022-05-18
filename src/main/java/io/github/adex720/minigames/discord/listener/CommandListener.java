package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author adex720
 */
public class CommandListener extends ListenerAdapter {

    private final MinigamesBot bot;

    private final CommandManager commandManager;

    public CommandListener(MinigamesBot bot, CommandManager commandManager) {
        this.bot = bot;
        this.commandManager = commandManager;
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            // Discord has a limit for how often a bot can dm a user.
            // There isn't any lost for not being able to use commands on dms anyways.
            return;
        }

        String commandName = event.getName();
        Member member = event.getInteraction().getMember();
        long userId = member.getIdLong(); // Only produces null pointer exception when command was used on dms.

        if (bot.getBanManager().isBanned(userId)) return; // Check if user is banned

        for (Command command : commandManager.MAIN_COMMANDS) {
            if (commandName.equals(command.getMainName())) {
                // If the used command was a subcommand event#getName() returns the name of parent command.
                // Execution of the subcommands is managed on the execute method of te parent command.

                event.deferReply().queue();
                // Each command always gives a reply (or should give)
                // Deferring the reply here allows more than one message to be sent easier.

                CommandInfo commandInfo = CommandInfo.create(event, bot);
                if (!command.onRun(event, commandInfo)) {
                    bot.getLogger().error("Failed to run command {}, info: {}", command.getFullName(), commandInfo); // Log failure
                }
                break;
            }
        }

    }
}
