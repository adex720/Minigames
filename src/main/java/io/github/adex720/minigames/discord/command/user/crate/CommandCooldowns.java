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

public class CommandCooldowns extends Command {

    public CommandCooldowns(MinigamesBot bot) {
        super(bot, "cooldowns", "Views your cooldowns for kits", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        User user = ci.author();
        event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("COOLDOWNS")
                        .addField("Hint", "You can claim all of your kits with `/claim`\nYou can claim one booster with its own command.\nExample: `/daily`.", true)
                        .addField("Kits", ci.profile().getKitCooldowns(), true)
                        .setColor(Util.getColor(user.getIdLong()))
                        .setFooter(user.getName(), user.getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build()).queue();
        return true;
    }
}