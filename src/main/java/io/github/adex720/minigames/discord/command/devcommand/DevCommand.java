package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A command that can only be run be the developer.
 *
 * @author adex720
 */
public abstract class DevCommand {

    protected final MinigamesBot bot;
    public final String name;

    public DevCommand(MinigamesBot bot, String name) {
        this.bot = bot;
        this.name = name;
    }

    public abstract boolean onRun(MessageReceivedEvent event);
}
