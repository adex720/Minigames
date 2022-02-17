package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.HashMap;

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
        Member presser = event.getInteraction().getMember();

        MinigameType<? extends Minigame> type = WAITING.remove(presser.getIdLong());

        if (type != null) {
            Minigame minigame = type.create(event, ci);
            if (minigame != null) {
                bot.getMinigameManager().addMinigame(minigame);
            }
            event.reply(type.getReplyForInvalidStartState()).queue();
        }
    }
}
