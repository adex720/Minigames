package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DevCommandShutdown extends DevCommand {

    public DevCommandShutdown(MinigamesBot bot) {
        super(bot, "shutdown");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        bot.save();
        event.getChannel().sendMessage("Saved all data. Shutting down!").queue();
        bot.stop();
        return true;
    }
}
