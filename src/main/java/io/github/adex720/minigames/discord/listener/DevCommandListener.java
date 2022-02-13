package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.devcommand.DevCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DevCommandListener extends ListenerAdapter {

    private final Set<DevCommand> COMMANDS;
    private final String prefix;

    private final MinigamesBot bot;
    private final long developerId;

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
                if (command.onRun(event)) {
                    bot.getLogger().info("Used developer command " + command.name);
                    return;
                } else {
                    bot.getLogger().error("Failed to run dev command " + command.name);
                }
            }
        }
    }

    public void addCommand(DevCommand command) {
        COMMANDS.add(command);
    }
}
