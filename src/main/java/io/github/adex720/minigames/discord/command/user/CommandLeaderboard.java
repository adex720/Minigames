package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandLeaderboard extends Command {

    public CommandLeaderboard(MinigamesBot bot) {
        super(bot, "leaderboard", "Shows the leaderboard of a category.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return false;
    }

    @Override
    protected CommandData createCommandData() {
        CommandData commandData = super.createCommandData();

        OptionData optionData = new OptionData(OptionType.STRING, "category", "Category to show.", true);
        for (Stat stat : bot.getStatManager().getLeaderboardStats()) {
            String name = stat.name();
            optionData.addChoice((char) (name.charAt(0) - 32) + name.substring(1), name);
        }

        commandData.addOptions(optionData)
                .addOption(OptionType.INTEGER, "page", "Page to show.", false);
        return commandData;
    }
}
