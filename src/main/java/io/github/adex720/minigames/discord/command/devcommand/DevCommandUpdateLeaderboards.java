package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DevCommandUpdateLeaderboards extends DevCommand{

    public DevCommandUpdateLeaderboards(MinigamesBot bot) {
        super(bot, "update-leaderboards");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Starting to update all leaderboards.").queue();
        bot.getStatManager().updateLeaderboards();
        event.getChannel().sendMessage("Updated all leaderboards.").queue();
        return true;
    }
}
