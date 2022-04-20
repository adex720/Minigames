package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Saves all data and disconnects the bot from Discord.
 *
 * @author adex720
 */
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
        // A shutdown can take multiple minutes because each timer with a low delay isn't stored to be stopped.
        // The disconnection from Discord starts right after saving all data.
    }
}
