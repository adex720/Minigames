package io.github.adex720.minigames.gameplay.manager.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Manages minigame replay feature.
 * After finishing a minigame, a new minigame of same type can be started by pressing the replay button.
 * Only a player who played the previous minigame can use the button.
 *
 * @author adex720
 */
public class ReplayManager extends ButtonManager {

    public ReplayManager(MinigamesBot bot) {
        super(bot, "replay", true);
    }

    @Override
    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo ci, String[] args) {
        String minigameName = args[1];
        long gameId = Long.parseLong(args[2]);

        if (ci.gameId() != gameId)
            return; // Button is not for the player/party. This is to prevent accidental new games.

        MinigameType<? extends Minigame> type = bot.getMinigameTypeManager().getType(minigameName);

        if (type != null) {
            if (!type.canStart(ci)) {
                event.reply(type.getReplyForInvalidStartState(ci)).queue();
                return;
            }

            Replyable replyable = Replyable.from(event);
            Minigame minigame = type.create(replyable, ci);
            if (minigame != null) {
                bot.getMinigameManager().addMinigame(minigame);
                return;
            }
            event.reply(type.getReplyForNullAfterConstructor(ci)).queue();
            return;
        }

        bot.getLogger().error("Unknown minigame type to replay:{}", minigameName);
    }
}
