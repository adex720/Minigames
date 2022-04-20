package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Disconnects the bot from Discord but doesn't save data.
 *
 * @author adex720
 */
public class DevCommandTerminate extends DevCommand {

    public DevCommandTerminate(MinigamesBot bot) {
        super(bot, "terminate");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Shutting down without saving date!").queue();
        bot.stop();
        return true;
        // A shutdown can take multiple minutes because each timer with a low delay isn't stored to be stopped.
        // The disconnection from Discord starts right after saving all data.
    }
}
