package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Reloads everything from last save state.
 * */
public class DevCommandReloadData extends DevCommand {

    public DevCommandReloadData(MinigamesBot bot) {
        super(bot, "reload-data");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        bot.reload();
        event.getChannel().sendMessage("Reloaded all data. Unsaved data was lost!!!").queue();
        return true;
    }
}
