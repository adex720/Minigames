package io.github.adex720.minigames.minigame.gamble;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.Replyable;

/**
 * @author adex720
 */
public abstract class GambleMinigame extends Minigame {

    public final int bet;
    public final float betMultiplier;

    public GambleMinigame(MinigamesBot bot, GambleMinigameType<? extends GambleMinigame> type, Profile profile, long id, long lastActive, int bet) {
        super(bot, type, id, false, lastActive);

        this.bet = bet;
        profile.removeCoins(bet); // Bet is removed, so you can't quit on bad situations

        this.betMultiplier = type.betMultiplier;
    }

    @Override
    public void appendQuest(Replyable replyable, Profile profile, boolean won) {
        super.appendQuest(replyable, profile, won);

        //TODO: add quest for gambling
    }

    @Override
    public String addRewards(Replyable replyable, Profile profile, boolean won) {
        if (won) {
            int reward = (int) (bet * betMultiplier);
            profile.addCoins(bet + reward, false, replyable);
            return "You won and received back your bet and " + reward + " coins!";
        }

        return "You lost the game and your bet!";
    }

    @Override
    public void appendStats(Profile profile, boolean won) {
        super.appendStats(profile, won);

        profile.increaseStat("bets won", bet);
    }
}
