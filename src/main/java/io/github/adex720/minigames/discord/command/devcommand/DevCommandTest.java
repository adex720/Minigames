package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Code here can be modified to test something with a command.
 *
 * @author adex720
 */
public class DevCommandTest extends DevCommand {

    public DevCommandTest(MinigamesBot bot) {
        super(bot, "test");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        doStuff(event);
        return true;
    }

    private void doStuff(MessageReceivedEvent event) {

        event.getChannel().sendMessage("<:2H:992579076600643725>").queue();

    }
}
