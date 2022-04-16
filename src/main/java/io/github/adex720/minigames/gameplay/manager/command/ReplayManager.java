package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.HashMap;

/**
 * Manages minigame replay feature.
 * After finishing a minigame, a new minigame of same type can be started
 * by pressing the replay button during the next minute.
 */
public class ReplayManager extends Manager {

    private final HashMap<Long, MinigameType<? extends Minigame>> WAITING;

    public ReplayManager(MinigamesBot bot) {
        super(bot, "replay-manager");

        WAITING = new HashMap<>();
    }

    public void addReplay(long id, MinigameType<? extends Minigame> type) {
        WAITING.put(id, type);
        Util.schedule(() -> WAITING.remove(id, type), 60000);
    }

    public void onButtonPress(ButtonClickEvent event, CommandInfo ci) {
        User presser = ci.author();

        MinigameType<? extends Minigame> type = WAITING.remove(presser.getIdLong());

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
        }
    }
}
