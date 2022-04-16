package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Saves all data
 * */
public class DevCommandUpdateLineCount extends DevCommand {

    public DevCommandUpdateLineCount(MinigamesBot bot) {
        super(bot, "update-linecount");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        bot.calculateLinesOfCode();
        event.getChannel().sendMessage("Updated linecount.").queue();
        return true;
    }
}
