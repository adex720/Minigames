package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * This Listener listens for {@link GuildJoinEvent}.
 * The only job this listener has is to send a greeting message when the bot joins a new server.
 * */
public class GuildJoinListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public GuildJoinListener(MinigamesBot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();

        MessageChannel channel = guild.getDefaultChannel();

        if (channel == null) {
            List<NewsChannel> newsChannels = guild.getNewsChannels();
            if (!newsChannels.isEmpty()) {
                channel = newsChannels.get(0);
                channel.sendMessageEmbeds(createGreetingsMessage()).queue();
                return;
            }

            List<TextChannel> channels = guild.getTextChannels();
            if (!channels.isEmpty()) {
                channel = channels.get(0);
            } else {
                return;
            }
        }

        channel.sendMessageEmbeds(createGreetingsMessage()).queue();
    }

    public MessageEmbed createGreetingsMessage() {
        return new EmbedBuilder()
                .setTitle("Thank you for inviting me!")
                .setColor(Util.MINIGAMES_COLOR)
                .addField("Use slash commands to interact with me", "- Use `/help` to see list of commands." +
                        "\n- Use `/start` to create a profile.", false)
                .setFooter(bot.getJda().getSelfUser().getName(), bot.getJda().getSelfUser().getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }
}
