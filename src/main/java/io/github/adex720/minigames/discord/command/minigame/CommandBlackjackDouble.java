package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.gamble.blackjack.MinigameBlackjack;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * @author adex720
 */
public class CommandBlackjackDouble extends MinigameSubcommand {

    public CommandBlackjackDouble(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.BLACKJACK, "double", "Doubles the bet, hits one card and the stands.");
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().BLACKJACK) {
                MinigameBlackjack blackjack = (MinigameBlackjack) minigame;

                if (!blackjack.canDouble()) {
                    event.getHook().sendMessage("You can only double on the first turn!").queue();
                    return true;
                }

                blackjack.hitDouble(Replyable.from(event), ci);
                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing blackjack game!").queue();
        return true;
    }
}
