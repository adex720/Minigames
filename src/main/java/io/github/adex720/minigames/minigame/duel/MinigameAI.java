package io.github.adex720.minigames.minigame.duel;

import io.github.adex720.minigames.MinigamesBot;

public abstract class MinigameAI<M extends DuelMinigame> {

    protected final MinigamesBot bot;

    protected MinigameAI(MinigamesBot bot) {
        this.bot = bot;
    }

    public abstract void makeMove(DuelMinigame minigame);
}
