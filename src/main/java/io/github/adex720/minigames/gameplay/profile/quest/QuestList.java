package io.github.adex720.minigames.gameplay.profile.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author adex720
 */
public class QuestList {

    public static final int AMOUNT_OF_QUESTS = 5;

    private final MinigamesBot bot;

    private final HashMap<Integer, QuestType> TYPES;
    private final HashMap<Integer, QuestDifficulty> DIFFICULTIES;

    private int typesAmount;
    private int difficultiesAmount;

    public QuestList(MinigamesBot bot) {
        this.bot = bot;

        TYPES = new HashMap<>();
        DIFFICULTIES = new HashMap<>();

        typesAmount = 0;
        difficultiesAmount = 0;

        init();
    }

    private void init() {
        JsonObject questsJson = bot.getResourceJson("quests").getAsJsonObject();

        JsonArray difficultiesJson = JsonHelper.getJsonArray(questsJson, "difficulties");
        for (JsonElement difficulty : difficultiesJson) {
            addDifficulty(difficulty.getAsJsonObject());
        }

        JsonArray typesJson = JsonHelper.getJsonArray(questsJson, "types");
        for (JsonElement type : typesJson) {
            JsonObject typeJson = type.getAsJsonObject();

            switch (JsonHelper.getString(typeJson, "type")) {
                case "minigame" -> addTypeMinigame(typeJson);
                case "coin" -> addTypeEarnCoins(typeJson);
                case "crate" -> addTypeOpenCrates(typeJson);
                case "booster" -> addTypeUseBoosters(typeJson);
                case "kit" -> addTypeClaimKits(typeJson);

                default -> bot.getLogger().error("Invalid quest category \"{}\" on quest type id {}!", typeJson.get("type"), typeJson.get("id"));
            }
        }

        bot.getLogger().info("Loaded quest difficulties and types!");
    }

    public void addTypeMinigame(JsonObject typeJson) {
        boolean win = JsonHelper.getBoolean(typeJson, "win", false);
        boolean party = JsonHelper.getBoolean(typeJson, "party", false);
        boolean minigame = typeJson.has("minigame");

        int id = JsonHelper.getInt(typeJson, "id");
        JsonObject textJson = JsonHelper.getJsonObject(typeJson, "name");
        String textStart = JsonHelper.getString(textJson, "start");
        String textEnd = JsonHelper.getString(textJson, "end");
        String name = JsonHelper.getString(textJson, "name", textStart + " " + textEnd);
        String hint = JsonHelper.getString(typeJson, "hint");

        JsonArray goalsJson = JsonHelper.getJsonArray(typeJson, "goals");
        int[] goals = JsonHelper.jsonArrayToIntArray(goalsJson);

        QuestType type;
        if (win) {
            if (party) {
                type = QuestType.winPartyMinigame(id, name, textStart, textEnd, hint, goals);
            } else if (!minigame) {
                type = QuestType.winAny(id, name, textStart, textEnd, hint, goals);
            } else {
                type = QuestType.winMinigame(id, name, textStart, textEnd, hint, goals, JsonHelper.getString(typeJson, "minigame"));
            }
        } else {
            if (party) {
                type = QuestType.playPartyMinigame(id, name, textStart, textEnd, hint, goals);
            } else if (!minigame) {
                type = QuestType.playAny(id, name, textStart, textEnd, hint, goals);
            } else {
                type = QuestType.playMinigame(id, name, textStart, textEnd, hint, goals, JsonHelper.getString(typeJson, "minigame"));
            }
        }

        addType(type);
    }

    public void addTypeEarnCoins(JsonObject typeJson) {
        int id = JsonHelper.getInt(typeJson, "id");
        JsonObject textJson = JsonHelper.getJsonObject(typeJson, "name");
        String textStart = JsonHelper.getString(textJson, "start");
        String textEnd = JsonHelper.getString(textJson, "end");
        String name = JsonHelper.getString(textJson, "name", textStart + " " + textEnd);
        String hint = JsonHelper.getString(typeJson, "hint");

        JsonArray goalsJson = JsonHelper.getJsonArray(typeJson, "goals");
        int[] goals = JsonHelper.jsonArrayToIntArray(goalsJson);


        QuestType type = QuestType.earnCoins(id, name, textStart, textEnd, hint, goals);
        addType(type);
    }

    public void addTypeOpenCrates(JsonObject typeJson) {
        boolean hasRarity = typeJson.has("rarity");

        int id = JsonHelper.getInt(typeJson, "id");
        JsonObject textJson = JsonHelper.getJsonObject(typeJson, "name");
        String textStart = JsonHelper.getString(textJson, "start");
        String textEnd = JsonHelper.getString(textJson, "end");
        String name = JsonHelper.getString(textJson, "name", textStart + " " + textEnd);
        String hint = JsonHelper.getString(typeJson, "hint");

        JsonArray goalsJson = JsonHelper.getJsonArray(typeJson, "goals");
        int[] goals = JsonHelper.jsonArrayToIntArray(goalsJson);


        QuestType type;
        if (hasRarity) {
            type = QuestType.openCrates(id, name, textStart, textEnd, hint, goals, JsonHelper.getString(typeJson, "rarity"));
        } else {
            type = QuestType.openCrates(id, name, textStart, textEnd, hint, goals);
        }

        addType(type);
    }

    public void addTypeUseBoosters(JsonObject typeJson) {
        int id = JsonHelper.getInt(typeJson, "id");
        JsonObject textJson = JsonHelper.getJsonObject(typeJson, "name");
        String textStart = JsonHelper.getString(textJson, "start");
        String textEnd = JsonHelper.getString(textJson, "end");
        String name = JsonHelper.getString(textJson, "name", textStart + " " + textEnd);
        String hint = JsonHelper.getString(typeJson, "hint");

        JsonArray goalsJson = JsonHelper.getJsonArray(typeJson, "goals");
        int[] goals = JsonHelper.jsonArrayToIntArray(goalsJson);


        QuestType type = QuestType.useBoosters(id, name, textStart, textEnd, hint, goals);
        addType(type);
    }

    public void addTypeClaimKits(JsonObject typeJson) {
        boolean hasKit = typeJson.has("kit");

        int id = JsonHelper.getInt(typeJson, "id");
        JsonObject textJson = JsonHelper.getJsonObject(typeJson, "name");
        String textStart = JsonHelper.getString(textJson, "start");
        String textEnd = JsonHelper.getString(textJson, "end");
        String name = JsonHelper.getString(textJson, "name", textStart + " " + textEnd);
        String hint = JsonHelper.getString(typeJson, "hint");

        JsonArray goalsJson = JsonHelper.getJsonArray(typeJson, "goals");
        int[] goals = JsonHelper.jsonArrayToIntArray(goalsJson);


        QuestType type;
        if (hasKit) {
            type = QuestType.claimKits(id, name, textStart, textEnd, hint, goals, JsonHelper.getString(typeJson, "kit"));
        } else {
            type = QuestType.claimKits(id, name, textStart, textEnd, hint, goals);
        }

        addType(type);
    }


    public void addType(QuestType type) {
        TYPES.put(typesAmount, type);
        typesAmount++;
    }

    public void addDifficulty(JsonObject difficultyJson) {
        int id = JsonHelper.getInt(difficultyJson, "id");
        String name = JsonHelper.getString(difficultyJson, "name");
        int rewardCoins = JsonHelper.getInt(difficultyJson, "coins");
        int rewardCrateId = JsonHelper.getInt(difficultyJson, "crate");

        addDifficulty(new QuestDifficulty(name, rewardCoins, rewardCrateId, id));
    }

    public void addDifficulty(QuestDifficulty difficulty) {
        DIFFICULTIES.put(difficulty.id, difficulty);
        difficultiesAmount++;
    }

    public ArrayList<Quest> generateQuests(int[] types, int[] difficulties) {
        ArrayList<Quest> quests = new ArrayList<>();
        for (int i = 0; i < AMOUNT_OF_QUESTS; i++) {
            quests.add(new Quest(TYPES.get(types[i]), DIFFICULTIES.get(difficulties[i])));
        }
        return quests;
    }

    public QuestType getType(int id) {
        return TYPES.get(id);
    }

    public QuestDifficulty getDifficulty(int id) {
        return DIFFICULTIES.get(id);
    }


}
