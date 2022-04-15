package io.github.adex720.minigames.gameplay.manager.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.SavableManager;
import io.github.adex720.minigames.gameplay.profile.quest.Quest;
import io.github.adex720.minigames.gameplay.profile.quest.QuestList;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages {@link Quest}s
 */
public class QuestManager extends SavableManager<Quest> {

    public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    private final HashMap<Long, ArrayList<Quest>> QUESTS;

    public QuestManager(MinigamesBot bot) {
        super(bot, "quest-manager");
        QUESTS = new HashMap<>();
    }

    @Override
    public Quest fromJson(JsonObject json) {
        return Quest.fromJson(bot.getQuestList(), json);
    }

    public void addQuestsFromJson(long userId, JsonArray questsJson) {

        ArrayList<Quest> questArrayList = QUESTS.computeIfAbsent(userId, k -> new ArrayList<>(5));  // Creating new ArrayList for quests if one doesn't exist

        for (JsonElement questJson : questsJson) {
            questArrayList.add(fromJson(questJson.getAsJsonObject()));
        }
    }

    /**
     * Returns null when quests aren't loaded
     */
    @Nullable
    public JsonArray getQuestJson(long userId) {
        ArrayList<Quest> quests = QUESTS.get(userId);

        if (quests == null) return null;

        JsonArray questsJson = new JsonArray();

        quests.forEach(quest -> questsJson.add(quest.getAsJson()));

        return questsJson;
    }

    /**
     * Quests are saves on profiles.
     *
     * @return an empty HashSet.
     */
    @Override
    public Set<Quest> getValues() {
        return new HashSet<>();
    }

    public ArrayList<Quest> getQuests(long id) {
        return QUESTS.get(id);
    }

    public boolean hasLoadedQuests(long id) {
        return QUESTS.containsKey(id);
    }

    public ArrayList<Quest> getQuestsOrGenerate(long id) {
        ArrayList<Quest> quests = QUESTS.get(id);
        if (quests != null) return quests;

        return generateQuests(id);
    }


    // Generates quests if they are not yet generated
    public MessageEmbed.Field getProgress(long id) {
        ArrayList<Quest> quests = getQuestsOrGenerate(id);

        int finished = (int) quests.stream().filter(Quest::isCompleted).count();

        String title;
        if (finished == QuestList.AMOUNT_OF_QUESTS) {
            title = "All of your quests are completed! You will receive new quests tomorrow.\n";
        } else {
            title = "Quests (" + finished + "/" + QuestList.AMOUNT_OF_QUESTS + " finished)";
        }

        StringBuilder questsString = new StringBuilder();
        for (Quest quest : quests) {
            questsString.append(quest);
            questsString.append('\n');
        }

        return new MessageEmbed.Field(title, questsString.toString(), false);
    }

    /**
     * Generates new quests for the given userId.
     * If quests for the user are already generated, nothing is done
     */
    public void generateQuestsIfNotGenerated(long userId){
        if (QUESTS.containsKey(userId)) return; // Already generated

        generateQuests(userId);
    }

    /**
     * Generates quests for the given userId.
     * The quests loop in a group of 16 different combinations.
     * The quests depend on current day and the last 4 bits of the user id.
     */
    public ArrayList<Quest> generateQuests(long userId) {
        int specifier = (int) (userId & 0xF);

        long day = System.currentTimeMillis() / MILLISECONDS_IN_DAY;

        boolean tripleDifficulty = (specifier & 0x3) == (day & 0x3);
        // If true user has 3 quests from the difficulty and 1 from others
        // If false user has 1 quest from the difficulty and 2 from others


        int difficulty = 1; // normal

        int difficultyInt = (int) ((specifier & 0xC) ^ (day & 0xC) >> 2);

        if (difficultyInt == 0) difficulty = 0;      // easy
        else if (difficultyInt == 3) difficulty = 2; // hard
        else if ((day & 0x3) == 0) { // balancing appearance likelihoods
            if (difficultyInt == 1) difficulty = 0;
            else difficulty = 2;
        }

        int endOfDay = (int) (day & 0xFF);

        int code = endOfDay + specifier;

        int[] types = new int[5];

        types[0] = shuffle(code); // Generating a quest type for each quest slot.
        types[1] = shuffle((code ^ 0xF) & 0xF);
        types[2] = (types[0] ^ 0xF) & 0xF;
        types[3] = shuffle(types[0]);
        types[4] = (types[0] ^ 0x1) & 0xF;

        int checking = 1;

        while (checking < 5) { // Removing duplicate quests
            boolean valid = true;

            for (int i = 0; i < checking; i++) {
                if (types[i] == types[checking]) {
                    types[checking] = (types[checking] + 1) & 0xF;
                    valid = false;
                    break;
                }
            }

            if (valid) {
                checking++;
            }
        }

        int[] difficulties = getDifficultiesArray(tripleDifficulty, difficulty);
        ArrayList<Quest> quests = bot.getQuestList().generateQuests(types, difficulties);
        QUESTS.put(userId, quests);
        return quests;
    }

    /**
     * Shuffles 4 bits to make quests less repetitive
     */
    public static int shuffle(int original) {
        int first = (original & 0x8) >> 3;
        int second = (original & 0x4) >> 2;
        int third = (original & 0x2) >> 1;
        int fourth = (original & 0x1);

        return (first ^ third) << 3 | (third) << 2 | (fourth) << 1 | (second ^ fourth);
    }

    /**
     * Creates an array containing the quest difficulties.
     *
     * @param tripleDifficulty should there be 3 quests from the same difficulty.
     * @param baseDifficulty   which difficulty has 1 or 3 quests.
     * @return quests difficulty ids on rising order.
     */
    public static int[] getDifficultiesArray(boolean tripleDifficulty, int baseDifficulty) {
        int[] difficulties = new int[5];

        difficulties[0] = 0;
        difficulties[4] = 2;

        difficulties[1] = 1;
        difficulties[2] = 1;
        difficulties[3] = 1;

        if (tripleDifficulty) {
            if (baseDifficulty == 0) {
                difficulties[1] = 0;
                difficulties[2] = 0;
            } else if (baseDifficulty == 2) {
                difficulties[2] = 2;
                difficulties[3] = 2;
            }
        } else {
            if (baseDifficulty != 0) {
                difficulties[1] = 0;
            }
            if (baseDifficulty != 2) {
                difficulties[3] = 2;
            }
        }

        return difficulties;
    }

    public void unloadQuests() {
        QUESTS.clear();
        bot.getLogger().info("Unloaded quests!");
    }

    public void removeQuests(long id){
        QUESTS.remove(id);
    }

}
