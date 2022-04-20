package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.devcommand.DevCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Listens to different types of management commands.
 * The dev commands can only be run be te developer.
 * <p>
 * Examples of dev commands are saving data, updating leaderboards and shutting the bot down.
 *
 * @author adex720
 */
public class DevCommandListener extends ListenerAdapter {

    private final Set<DevCommand> COMMANDS;
    private final String prefix;

    private final MinigamesBot bot;
    private final long developerId;

    /**
     * Only user with the {@param developerId} can run these commands.
     */
    public DevCommandListener(MinigamesBot bot, long developerId, String prefix) {
        this.bot = bot;
        this.developerId = developerId;
        this.prefix = prefix;

        COMMANDS = new HashSet<>();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() != developerId) return;

        String content = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT);
        if (!content.startsWith(prefix)) return;

        String[] split = content.split(" ");
        String commandName = split[0].substring(prefix.length());

        for (DevCommand command : COMMANDS) {
            if (command.name.equals(commandName)) {
                if (command.onRun(event)) { // execute command
                    bot.getLogger().info("Used developer command " + command.name); // Log usage of command
                } else {
                    bot.getLogger().error("Failed to run dev command " + command.name); // Log failure
                }
                return;
            }
        }
    }

    /**
     * @param command Command to add on Dev commands list.
     * */
    public void addCommand(DevCommand command) {
        COMMANDS.add(command);
    }
}
