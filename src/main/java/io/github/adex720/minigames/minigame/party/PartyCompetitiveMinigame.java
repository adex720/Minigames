package io.github.adex720.minigames.minigame.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * @author adex720
 */
public abstract class PartyCompetitiveMinigame extends Minigame {

    public PartyCompetitiveMinigame(MinigamesBot bot, MinigameType<? extends Minigame> type, long id, long lastActive) {
        super(bot, type, id, true, lastActive);
    }

    /**
     * @return the winners of the minigame.
     * If no one has won an empty array should be returned.
     */
    public abstract long[] getWinners();

    @Override
    public boolean requiresLockedParty() {
        return true;
    }
}
