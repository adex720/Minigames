package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

/**
 * Manages minigame replay feature.
 * After finishing a minigame, a new minigame of same type can be started by pressing the replay button.
 * Only a player who played the previous minigame can use the button.
 *
 * @author adex720
 */
public class ReplayManager extends Manager {

    public ReplayManager(MinigamesBot bot) {
        super(bot, "replay-manager");
    }

    public void onButtonPress(ButtonClickEvent event, CommandInfo ci, String[] args) {
        String minigameName = args[1];
        long gameId = Long.parseLong(args[2]);

        if (ci.gameId() != gameId)
            return; // Button is not for the player/party. This is to prevent accidental new games.

        MinigameType<? extends Minigame> type = bot.getMinigameTypeManager().getType(minigameName);

        if (type != null) {
            if (!type.canStart(ci)) {
                event.reply(type.getReplyForInvalidStartState()).queue();
                return;
            }

            Minigame minigame = type.create(event, ci);
            if (minigame != null) {
                bot.getMinigameManager().addMinigame(minigame);
                return;
            }
            event.reply(type.getReplyForInvalidStartState()).queue();
            return;
        }

        bot.getLogger().error("Unknown minigame type to replay:{}", minigameName);
    }
}
