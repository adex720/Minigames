package io.github.adex720.minigames.gameplay.profile.quest;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.JsonHelper;

public class Quest implements IdCompound, JsonSavable<Quest> {

    private final QuestType type;
    private final QuestDifficulty difficulty;

    private int progress;
    private final int goal;

    public Quest(QuestType type, QuestDifficulty difficulty) {
        this.type = type;
        this.difficulty = difficulty;
        progress = 0;
        goal = type.goals[difficulty.id];
    }

    private Quest(QuestList questList, int type, int difficulty, int progress) {
        this.type = questList.getType(type);
        this.difficulty = questList.getDifficulty(difficulty);
        this.progress = progress;
        goal = this.type.goals[difficulty];
    }

    public String getTextForMessage() {
        return type.textStart + " " + goal + " " + type.textEnd + ". Current progress: " + progress;
    }

    /**
     * Always returns -1, quests are saved on profiles and not as own json.
     */
    @Override
    public Long getId() {
        return -1L;
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.type.id);
        json.addProperty("difficulty", this.difficulty.id);
        json.addProperty("progress", this.progress);

        return json;
    }

    public static Quest fromJson(QuestList questList, JsonObject json) {
        int type = JsonHelper.getInt(json, "type");
        int difficulty = JsonHelper.getInt(json, "difficulty");
        int progress = JsonHelper.getInt(json, "progress", 0);

        return new Quest(questList, type, difficulty, progress);
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

    public void crateOpened(CrateType rarity, Profile profile) {
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
