package io.github.adex720.minigames.minigame.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * @author adex720
 */
public abstract class PartyCompetitiveMinigameType<T extends PartyCompetitiveMinigame> extends MinigameType<T> {

    protected PartyCompetitiveMinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, int minPartySize) {
        super(bot, typeManager, name, true, minPartySize);
    }

    @Override
    public String getReplyForInvalidStartState() {
        return "This minigame requires a party with a minimum size of " + minPartySize + " to be played!";
    }
}
