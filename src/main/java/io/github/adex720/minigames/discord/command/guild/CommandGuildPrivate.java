package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandGuildPrivate extends Subcommand {

    public CommandGuildPrivate(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "private", "Makes the guild private requiring people to be invited in order to join.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        if (!ci.isGuildOwner()) {
            event.getHook().sendMessage("Only the guild owner can make the party private!").setEphemeral(true).queue();
            return true;
        }

        guild.setPublicity(false);
        event.getHook().sendMessage("You set your guild as private! New people must be invited in order to be able to join.").queue();
        return true;
    }
}
