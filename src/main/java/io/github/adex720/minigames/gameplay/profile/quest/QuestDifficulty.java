package io.github.adex720.minigames.gameplay.profile.quest;

public class QuestDifficulty {

    public final String name;
    public final int reward;

    public final int difficultyId;

    public QuestDifficulty(String name, int reward, int difficultyId) {
        this.name = name;
        this.reward = reward;
        this.difficultyId = difficultyId;
    }
}
