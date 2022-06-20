package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 * @author adex720
 */
public class CommandListener extends ListenerAdapter {

    private final MinigamesBot bot;

    private final CommandManager commandManager;

    private final HashMap<Long, Long> COOLDOWNS;
    public static final long COMMAND_COOLDOWN = 750;

    public static final OffsetDateTime ZERO = LocalDateTime.of(1970, 1, 1, 0, 0, 0).atOffset(ZoneOffset.UTC);

    public CommandListener(MinigamesBot bot, CommandManager commandManager) {
        this.bot = bot;
        this.commandManager = commandManager;
        COOLDOWNS = new HashMap<>();
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            // Discord has a limit for how often a bot can dm a user.
            // There isn't any lost for not being able to use commands on dms anyways.
            return;
        }

        String commandName = event.getName();
        long userId = event.getUser().getIdLong();

        if (bot.getBanManager().isBanned(userId)) return; // Check if user is banned

        long timestamp = ZERO.until(event.getInteraction().getTimeCreated(), ChronoUnit.MILLIS);
        if (isOnCooldown(userId, timestamp)) return; // Ignore commands sent too fast

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
                    return;
                }
                startCooldown(userId, timestamp);
                return;
            }
        }

    }

    private boolean isOnCooldown(long userId, long interactionTimestamp) {
        Long lastInteraction = COOLDOWNS.get(userId);
        if (lastInteraction == null) return false;
        return lastInteraction + COMMAND_COOLDOWN > interactionTimestamp;
    }

    private void startCooldown(long userId, long interactionTimestamp) {
        COOLDOWNS.put(userId, interactionTimestamp);
        Util.schedule(() -> COOLDOWNS.remove(userId, interactionTimestamp), COMMAND_COOLDOWN);
    }
}
