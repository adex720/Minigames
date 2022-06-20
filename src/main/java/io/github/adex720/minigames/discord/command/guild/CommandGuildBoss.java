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
public class CommandGuildBoss extends Subcommand {

    public CommandGuildBoss(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "boss", "Displays the guild boss.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You aren't in a guild!").queue();
            return true;
        }

        event.getHook().sendMessageEmbeds(guild.getBossMessage(ci.author())).queue();
        return true;
    }
}
