package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class SelfMentionListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public String mentionShort;
    public String mentionLong;

    public SelfMentionListener(MinigamesBot bot) {
        this.bot = bot;
    }

    public void init() {
        long id = bot.getJda().getSelfUser().getIdLong();
        mentionShort = "<@" + id + ">";
        mentionLong = "<@!" + id + ">";
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (!content.startsWith("<@")) return;

        if (!(content.equals(mentionShort) || content.equals(mentionLong))) return;

        User user = event.getAuthor();
        if (bot.getBanManager().isBanned(user.getIdLong())) return;

        event.getChannel().sendMessageEmbeds(getEmbed()).queue();
    }

    public MessageEmbed getEmbed() {
        User self = bot.getJda().getSelfUser();

        return new EmbedBuilder()
                .setTitle("MINIGAMES")
                .setColor(Util.MINIGAMES_COLOR)
                .addField("Thank you for playing me", """
                        - Use `/help` for list of commands.
                        - Use `/start` to create a profile.
                        - Use `/usage` for information about the bot.
                        """, false)
                .setFooter(self.getName(), self.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }
}
