package io.github.adex720.minigames.minigame.duel;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;

public abstract class DuelMinigameType<M extends DuelMinigame> extends MinigameType<M> {

    protected final boolean hasAI;
    protected final MinigameAI<? extends DuelMinigame> ai;

    protected DuelMinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, boolean hasAI, MinigameAI<? extends DuelMinigame> ai) {
        super(bot, typeManager, name, !hasAI, 2);
        this.hasAI = hasAI;
        this.ai = ai;
    }

    @Override
    public String getReplyForInvalidStartState() {
        return hasAI ? "Your party needs to have 2 members for you to play this minigame." :
                "This minigame can only be played on a party with size of 2.";
    }

}
