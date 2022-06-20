package io.github.adex720.minigames.minigame.gamble;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.replyable.Replyable;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author adex720
 */
public abstract class GambleMinigame extends Minigame {

    public final int bet;
    public float betMultiplier;

    /**
     * Removes bet from the profile if it's not null.
     */
    public GambleMinigame(MinigamesBot bot, GambleMinigameType<? extends GambleMinigame> type, @Nullable Profile profile, long id, long lastActive, int bet) {
        super(bot, type, id, false, lastActive);

        this.bet = bet;

        if (profile != null) profile.removeCoins(bet); // Bet is removed, so you can't quit on bad situations

        this.betMultiplier = type.betMultiplier;
    }

    @Override
    public void appendQuest(Replyable replyable, Profile profile, boolean won) {
        super.appendQuest(replyable, profile, won);

        if (won)
            profile.appendQuests(q -> q.moneyGambled(replyable, bet, profile), q -> q.betWon(replyable, bet, profile));
        else profile.appendQuests(q -> q.moneyGambled(replyable, bet, profile));
    }

    @Override
    public String addRewards(Replyable replyable, Profile profile, boolean won) {
        if (won) {
            int reward = getReward(bot.getRandom());
            profile.addCoins(bet + reward, false, replyable);
            return "You won and received back your bet and " + reward + " coins!";
        }

        return "Better luck next time!";
    }

    @Override
    public void appendStats(Profile profile, boolean won) {
        super.appendStats(profile, won);

        profile.increaseStat("bet won", bet);
    }

    /**
     * Calculates the amount of coins the players should be given with the bet on winning.
     *
     * @param random never used.
     * @return amount of coins won excluding original bet.
     */
    @Override
    public int getReward(@Nullable Random random) {
        return (int) (bet * betMultiplier);
    }

}
