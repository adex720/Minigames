package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandStats extends Command {

    public CommandStats(MinigamesBot bot) {
        super(bot, "stat", "Shows the stats of a user.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.getHook().sendMessage("This command is unfinished").queue();
        return true; // TODO: sends stats
    }

    @Override
    protected CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "user", "User to view stats from.", false);
    }
}
