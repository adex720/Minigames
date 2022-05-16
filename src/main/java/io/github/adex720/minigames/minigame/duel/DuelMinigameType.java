package io.github.adex720.minigames.minigame.duel;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * A {@link MinigameType} that must be played as a duel.
 *
 * @author adex720
 */
public abstract class DuelMinigameType<M extends DuelMinigame> extends MinigameType<M> {

    protected final boolean hasAI;
    protected final MinigameAI<? extends DuelMinigame> ai;

    protected DuelMinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, MinigameAI<? extends DuelMinigame> ai) {
        super(bot, typeManager, name, false, 2);
        this.hasAI = true;
        this.ai = ai;
    }

    protected DuelMinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name) {
        super(bot, typeManager, name, true, 2);
        this.hasAI = false;
        this.ai = null;
    }

    @Override
    public String getReplyForInvalidStartState() {
        return hasAI ? "Your party needs to have 2 members for you to play this minigame." :
                "This minigame can only be played on a party with size of 2.";
    }

    @Override
    public boolean canStart(CommandInfo commandInfo) {
        if (commandInfo.isInParty()) return commandInfo.party().size() == 2;

        return hasAI;
    }
}
