package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A command which contains the same message (excluding date and author data) each time.
 *
 * @author adex720
 */
public abstract class SimpleReplyCommand extends Command {

    protected final String reply;

    public SimpleReplyCommand(MinigamesBot bot, String name, String description, String reply, CommandCategory category) {
        super(bot, name, description, category);
        this.reply = reply;
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        event.getHook().sendMessage(reply).queue();
        return true;
    }
}
