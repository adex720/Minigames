package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandUsage extends Command {

    public CommandUsage(MinigamesBot bot) {
        super(bot, "usage", "Sends information about the bot stats and usage", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        int profiles = bot.getProfileManager().getProfilesAmount();
        int servers = (int) bot.getJda().getGuildCache().size();
        event.getHook().sendMessage(String.format("""
                -%s users have profile!
                -On %s servers!"
                -%s commands!
                -%s total commands!
                -%s lines of code!
                """, profiles, servers, bot.getCommandManager().getCommandAmount(), bot.getCommandManager().getAllCommandAmount(), bot.getLinesOfCodeTotal())).queue();
        return true;
    }
}
