package io.github.adex720.minigames.minigame.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * @author adex720
 */
public abstract class PartyTeamMinigame extends Minigame {

    public PartyTeamMinigame(MinigamesBot bot, MinigameType<? extends Minigame> type, long id, long lastActive) {
        super(bot, type, id, true, lastActive);
    }


}
