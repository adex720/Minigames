package io.github.adex720.minigames.discord.command.user.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Date;

public class CommandCrates  extends Command {

    public CommandCrates(MinigamesBot bot) {
        super(bot, "crates", "Views your crates.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        User user = ci.author();

        event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .addField("Your crates", ci.profile().getCrates(), false)
                        .setColor(Util.getColor(user.getIdLong()))
                        .setFooter(user.getName(), user.getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build()).queue();

        return true;
    }

}
