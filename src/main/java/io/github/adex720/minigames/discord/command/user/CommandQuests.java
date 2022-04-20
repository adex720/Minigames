package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandQuests extends Command {

    public CommandQuests(MinigamesBot bot) {
        super(bot, "quests", "Checks your quests", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        User user = ci.author();

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("QUESTS")
                .setColor(Util.getColor(ci.authorId()))
                .addField(ci.bot().getQuestManager().getProgress(ci.authorId()))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();

        return true;
    }
}
