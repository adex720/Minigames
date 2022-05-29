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
public class CommandGuildLeave extends Subcommand {

    public CommandGuildLeave(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "leave", "Leaves your guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        if (ci.isGuildOwner()) {
            if (guild.sizeWithoutOwner() > 0) {
                event.getHook().sendMessage("You are not the only member of your guild! Transfer it to someone else or delete it instead.").setEphemeral(true).queue();
                return true;
            }

            bot.getGuildManager().remove(guild.getId());
            event.getHook().sendMessage("You were the only member of the guild. " + guild.getName() + " was deleted.").queue();
            return true;
        }

        guild.removeMember(ci.authorId());
        ci.profile().guildLeft();
        event.getHook().sendMessage("You left " + guild.getName() + ".").queue();
        return true;
    }
}
