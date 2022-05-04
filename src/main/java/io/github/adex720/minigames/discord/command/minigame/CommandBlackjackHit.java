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
public class CommandBlackjackHit extends MinigameSubcommand {

    public CommandBlackjackHit(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.BLACKJACK, "hit", "Hits a new card");
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().BLACKJACK) {
                MinigameBlackjack blackjack = (MinigameBlackjack) minigame;

                if (!blackjack.canHit()) {
                    event.getHook().sendMessage("You can't hit. You should stand!").queue();
                    return true;
                }

                blackjack.hit(Replyable.from(event), ci);
                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing blackjack game!").queue();
        return true;
    }
}
