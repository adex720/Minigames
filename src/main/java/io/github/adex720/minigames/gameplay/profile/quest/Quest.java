package io.github.adex720.minigames.gameplay.profile.quest;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;

public class Quest implements IdCompound, JsonSavable<Quest> {

    private final QuestType type;
    private final QuestDifficulty difficulty;

    private int progress;
    private final int goal;

    public Quest(QuestType type, QuestDifficulty difficulty) {
        this.type = type;
        this.difficulty = difficulty;
        progress = 0;
        goal = type.goals[difficulty.difficultyId];
    }

    public String getTextForMessage() {
        return type.textStart + " " + goal + " " + type.textEnd + ". Current progress: " + progress;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public JsonObject getAsJson() {
        return null;
    }

    public void checkForCompletion() {
        if (progress < goal) return;

        // TODO: give rewards
    }

    public void append(int amount) {
        if (amount > 0) {
            progress += amount;
            checkForCompletion();
        }
    }

    public void minigamePlayed(MinigameType<? extends Minigame> type, Profile profile) {
        append(this.type.minigamePlayed(type, profile));
    }

    public void minigameWon(MinigameType<? extends Minigame> type, Profile profile) {
        append(this.type.minigameWon(type, profile));
    }

    public void coinsEarned(int amount, Profile profile) {
        append(this.type.coinsEarned(amount, profile));
    }

    public void crateOpened(Object rarity, Profile profile) {
        append(this.type.crateOpened(rarity, profile));
    }

    public void boosterUsed(Profile profile) {
        append(this.type.boosterUsed(profile));
    }

    public void kitClaimed(String kit, Profile profile) {
        append(this.type.kitClaimed(kit, profile));
    }

    public boolean isCompleted() {
        return progress >= goal;
    }

    @Override
    public String toString() {
        return "**" + difficulty.name + ": " + type.name + ":** (" + progress + "/" + goal + ")";
    }
}
