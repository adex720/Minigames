package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.higherlower.MinigameHigherLower;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandHigherLowerGuess extends MinigameSubcommand {

    public CommandHigherLowerGuess(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.HIGHER_OR_LOWER, "guess", "Guesses a number in a game of higher-or-lower.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().HIGHER_OR_LOWER) {
                MinigameHigherLower higherLower = (MinigameHigherLower) minigame;
                higherLower.guess(event,ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing higher or lower game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.NUMBER, "number", "Number to guess", true);
    }
}
