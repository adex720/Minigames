package io.github.adex720.minigames.minigame.gamble;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;

/**
 * @author adex720
 */
public abstract class GambleMinigameType<T extends GambleMinigame> extends MinigameType<T> {

    public final float betMultiplier;

    protected GambleMinigameType(MinigamesBot bot, MinigameTypeManager typeManager, String name, float betMultiplier) {
        super(bot, typeManager, name, false, 1);

        this.betMultiplier = betMultiplier;
    }

    @Override
    public boolean canStart(CommandInfo commandInfo) {
        return !commandInfo.isInParty();
    }

    @Override
    public String getReplyForInvalidStartState(CommandInfo commandInfo) {
        if (commandInfo.isInParty()) return "You can't gamble while being on a party!";

        return "You can't afford your bet";
    }

    @Override
    public String getReplyForNullAfterConstructor(CommandInfo commandInfo) {
        return "You can't afford your bet";
    }
}
