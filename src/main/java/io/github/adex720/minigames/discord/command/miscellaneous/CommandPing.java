package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author adex720
 */
public class CommandPing extends Command {

    public CommandPing(MinigamesBot bot) {
        super(bot, "ping", "Calculates the ping of the bot api.", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        event.getHook().sendMessage("Ping :ping_pong:!").queue(action -> {
            OffsetDateTime start = event.getInteraction().getTimeCreated(); // Getting timestamp for when slash command was used on Discord
            OffsetDateTime end = action.getTimeCreated(); // Getting timestamp for when bot sent first message
            long ping = start.until(end, ChronoUnit.MILLIS);
            action.editMessage("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms :ping_pong:").queue(); // Editing message to include ping
        });

        return true;
    }
}
