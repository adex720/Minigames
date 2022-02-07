package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CommandPing extends Command {

    public CommandPing(MinigamesBot bot) {
        super(bot, "ping", "Calculates the ping of the bot api.", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.reply("Ping :ping_pong:!").queue(action -> {
            OffsetDateTime start = event.getInteraction().getTimeCreated();
            AtomicReference<OffsetDateTime> end = new AtomicReference<>();
            action.editOriginal("Pong :ping_pong:!").queue(message -> {
                end.set(message.getTimeCreated());
                long ping = start.until(end.get(), ChronoUnit.MILLIS);
                action.editOriginal("Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms :ping_pong:").queue();
            });

        });

        return true;
    }
}
