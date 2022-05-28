package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandGuildPerks extends Subcommand {

    public CommandGuildPerks(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "perks", "Shows information about guild perks.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(guild.getName())
                .addField("Guild shop perks", "Note: The perks affect everyone on the guild, but only guild owner and elders can purchase something.", true)
                .setColor(Util.getColor(guild.getId()));

        for (MessageEmbed.Field field : guild.getPerkInfoFields()){
            embedBuilder.addField(field);
        }

        User user = ci.author();
        event.getHook().sendMessageEmbeds(embedBuilder
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }
}
