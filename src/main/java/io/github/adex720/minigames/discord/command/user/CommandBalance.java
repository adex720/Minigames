package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * @author adex720
 */
public class CommandBalance extends Command {

    public CommandBalance(MinigamesBot bot) {
        super(bot, "balance", "Shows your balance", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.getHook().sendMessage("You have " + ci.profile().getCoins() + " coins.").queue();
        return true;
    }
}
