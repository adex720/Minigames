package io.github.adex720.minigames.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.Objects;

public abstract class SimpleEmbedReplyCommand extends Command {

    protected final String reply;

    private final String title;
    private final String header;
    @Nullable
    private final Color color;

    public SimpleEmbedReplyCommand(MinigamesBot bot, String name, String description, String reply, String title, String header, Color color, CommandCategory category) {
        super(bot, name, description, category);
        this.reply = reply;
        this.title = title;
        this.header = header;
        this.color = color;
    }

    public SimpleEmbedReplyCommand(MinigamesBot bot, String name, String description, String reply, String title, String header, CommandCategory category) {
        super(bot, name, description, category);
        this.reply = reply;
        this.title = title;
        this.header = header;
        this.color = null;
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        User author = ci.author();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .addField(header, reply, false)
                .setColor(Objects.requireNonNullElseGet(color, () -> Util.getColor(author.getIdLong())))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant());

        event.replyEmbeds(builder.build()).queue();
        return true;
    }
}
