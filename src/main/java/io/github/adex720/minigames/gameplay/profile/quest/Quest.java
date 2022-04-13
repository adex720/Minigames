package io.github.adex720.minigames.gameplay.profile.quest;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * Each player has own daily quests.
 * The quests reset at UTC midnight and are started by using /quests for first time the day.
 * <p>
 * Quests have 16 different {@link QuestType}s.
 * Player can't have more than one quest of each type at once.
 * <p>
 * Quests have 3 different {@link QuestDifficulty}s.
 * Player always has at least one quest of each difficulty.
 */
public class Quest implements JsonSavable<Quest> {

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

    public boolean checkForCompletion(SlashCommandEvent event, Profile profile) {
        if (progress < goal) return false;

        applyRewards(event, profile);
        event.getHook().sendMessage("You completed your " + difficulty.name + " quest!").queue();
        return true;
    }

    public void applyRewards(SlashCommandEvent event, Profile profile) {
        int coins = difficulty.rewardCoins;
        int crateType = difficulty.rewardCrateId;

        profile.addCoins(coins, true, event);
        profile.addCrate(crateType);
    }

    public void append(SlashCommandEvent event, Profile profile, int amount) {
        if (isCompleted()) return;

        if (amount > 0) {
            progress += amount;
            if (checkForCompletion(event, profile)) {
                progress = goal;

                // TODO: check if all quests are completed
            }
        }
    }

    public void minigamePlayed(SlashCommandEvent event, MinigameType<? extends Minigame> type, Profile profile) {
        append(event, profile, this.type.minigamePlayed(type, profile));
    }

    public void minigameWon(SlashCommandEvent event, MinigameType<? extends Minigame> type, Profile profile) {
        append(event, profile, this.type.minigameWon(type, profile));
    }

    public void coinsEarned(SlashCommandEvent event, int amount, Profile profile) {
        append(event, profile, this.type.coinsEarned(amount, profile));
    }

    public void crateOpened(SlashCommandEvent event, CrateType rarity, Profile profile) {
        append(event, profile, this.type.crateOpened(rarity, profile));
    }

    public void boosterUsed(SlashCommandEvent event, Profile profile) {
        append(event, profile, this.type.boosterUsed(profile));
    }

    public void kitClaimed(SlashCommandEvent event, String kit, Profile profile) {
        append(event, profile, this.type.kitClaimed(kit, profile));
    }

    public boolean isCompleted() {
        return progress >= goal;
    }

    @Override
    public String toString() {
        return "**" + difficulty.name + ": " + type.name + ":** (" + progress + "/" + goal + ")";
    }
}
