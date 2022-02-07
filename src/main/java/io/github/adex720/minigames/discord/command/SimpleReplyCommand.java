package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public abstract class SimpleReplyCommand extends Command {

    protected final String reply;

    public SimpleReplyCommand(MinigamesBot bot, String name, String description, String reply, CommandCategory category) {
        super(bot, name, description, category);
        this.reply = reply;
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.reply(reply).queue();
        return true;
    }
}
