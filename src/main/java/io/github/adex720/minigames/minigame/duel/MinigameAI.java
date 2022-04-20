package io.github.adex720.minigames.minigame.duel;

import io.github.adex720.minigames.MinigamesBot;

import java.util.Random;

/**
 * An AI to play {@link DuelMinigame}
 *
 * @author adex720
 * */
public abstract class MinigameAI<M extends DuelMinigame> {

    protected final MinigamesBot bot;

    protected MinigameAI(MinigamesBot bot) {
        this.bot = bot;
    }

    public abstract void makeMove(Random random, DuelMinigame minigame);
}
