package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Saves all data
 * */
public class DevCommandSave extends DevCommand {

    public DevCommandSave(MinigamesBot bot) {
        super(bot, "save");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        bot.save();
        event.getChannel().sendMessage("Saved all data.").queue();
        return true;
    }
}
