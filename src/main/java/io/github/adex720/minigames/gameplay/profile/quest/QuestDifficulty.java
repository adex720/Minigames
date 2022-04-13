package io.github.adex720.minigames.gameplay.profile.quest;

public class QuestDifficulty {

    public final String name;
    public final int rewardCoins;
    public final int rewardCrateId;

    public final int id;

    public QuestDifficulty(String name, int rewardCoins, int rewardCrateId, int id) {
        this.name = name;
        this.rewardCoins = rewardCoins;
        this.rewardCrateId = rewardCrateId;
        this.id = id;
    }
}
