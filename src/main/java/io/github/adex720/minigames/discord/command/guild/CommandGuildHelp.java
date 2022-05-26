package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.PageCommand;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author adex720
 */
public class CommandGuildHelp extends Subcommand implements PageCommand {

    public CommandGuildHelp(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "help", "Displays information on how guilds work.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        return true;
    }

    @Override
    public void onPageMove(ButtonInteractionEvent event, CommandInfo ci, int page, String[] args) {

    }

    @Override
    public String getName() {
        return name;
    }
}
