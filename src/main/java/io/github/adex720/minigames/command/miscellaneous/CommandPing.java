package io.github.adex720.minigames.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.temporal.ChronoUnit;

public class CommandPing extends Command {

    public CommandPing(MinigamesBot bot) {
        super(bot, "ping", "Calculates the ping of the bot api.", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.reply("Pong :ping_pong:!").queue(m -> {
            long ping = event.getInteraction().getTimeCreated().until(m.getInteraction().getTimeCreated(), ChronoUnit.MILLIS);
            m.editOriginal("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });

        return true;
    }
}
