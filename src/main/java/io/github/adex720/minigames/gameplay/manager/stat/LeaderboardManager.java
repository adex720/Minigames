package io.github.adex720.minigames.gameplay.manager.stat;

import io.github.adex720.minigames.MinigamesBot;

/**
 * Manages leaderboards.
 *
 * @author adex720
 * */
public class LeaderboardManager extends Thread {

    public static final long minimalCooldown = 1000 * 10;

    private final MinigamesBot bot;

    private long lastUpdated;
    private boolean shouldUpdate;

    public LeaderboardManager(MinigamesBot bot) {
        super();
        setPriority(Thread.MIN_PRIORITY);
        setName("Leaderboard updater Thread");

        this.bot = bot;
        lastUpdated = 1;
        shouldUpdate = true;
    }

    @Override
    public void run() {
        if (shouldUpdate) {
            if (lastUpdated + minimalCooldown <= System.currentTimeMillis()) {
                shouldUpdate = false;
                bot.getStatManager().updateLeaderboards();
                lastUpdated = System.currentTimeMillis();
                shouldUpdate = true;
            }
        }
    }
}
