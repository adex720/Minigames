package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author adex720
 */
public class CommandStats extends Command {

    public CommandStats(MinigamesBot bot) {
        super(bot, "stats", "Shows the stats of a user.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.getHook().sendMessageEmbeds(ci.profile().getStatsMessage(ci.author())).queue();
        return true;
    }

    @Override
    protected CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "user", "User to view stats from.", false);
    }
}
