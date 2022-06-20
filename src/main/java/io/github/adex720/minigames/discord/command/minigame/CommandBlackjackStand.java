package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.gamble.blackjack.MinigameBlackjack;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandBlackjackStand extends MinigameSubcommand {

    public CommandBlackjackStand(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.BLACKJACK, "stand", "Stands from the game");
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().BLACKJACK) {
                MinigameBlackjack blackjack = (MinigameBlackjack) minigame;

                blackjack.stand(Replyable.from(event), ci);
                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing blackjack game!").queue();
        return true;
    }
}
