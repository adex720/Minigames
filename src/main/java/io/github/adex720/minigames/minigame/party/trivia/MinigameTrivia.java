package io.github.adex720.minigames.minigame.party.trivia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.network.HttpsRequester;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author adex720
 */
public class MinigameTrivia extends PartyCompetitiveMinigame {

    public static final int QUESTIONS_PER_GAME = 3;
    public static final int TIME_TO_ANSWER = 10000;

    private final HashMap<Long, Integer> scores;

    private int currentQuestionId;
    private final Question[] questions;

    private boolean paused;

    public MinigameTrivia(MinigamesBot bot, Party party, Replyable replyable, CommandInfo commandInfo) {
        super(bot, bot.getMinigameTypeManager().TRIVIA, party.getId(), System.currentTimeMillis());

        scores = new HashMap<>();
        initScores(party);

        currentQuestionId = -1;
        questions = loadQuestions(bot.getHttpsRequester(), bot.getLogger(), party.getId());
        queueNextQuestion(5, replyable, commandInfo);

        paused = false;
    }

    /**
     * @param scores Pair of each user with their id and score. The array must also contain users with 0 points.
     */
    public MinigameTrivia(MinigamesBot bot, long id, ArrayList<Pair<Long, Integer>> scores, int currentQuestionId, Question[] questions, long lastActive) {
        super(bot, bot.getMinigameTypeManager().TRIVIA, id, lastActive);

        this.scores = new HashMap<>();
        for (Pair<Long, Integer> score : scores) {
            this.scores.put(score.first, score.second);
        }

        this.currentQuestionId = currentQuestionId;
        this.questions = questions;

        paused = true;
        // Game is paused since making api request for each trivia game is bad.
        // Also, the game would continue immediately,
        // as when the data is loaded it can be over 15 minutes from the time it was saved on.
    }

    public MinigameTrivia(CommandInfo commandInfo, Replyable replyable) {
        this(commandInfo.bot(), commandInfo.party(), replyable, commandInfo);
    }

    /**
     * Makes a web request
     */
    private Question[] loadQuestions(HttpsRequester httpsRequester, Logger logger, long gameId) {
        String request = "https://opentdb.com/api.php?amount=" + QUESTIONS_PER_GAME + "&type=multiple&encode=url3986";

        JsonObject response;
        try {
            response = httpsRequester.requestJson(request).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Failed to request trivia questions!" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return new Question[0];
        }

        if (JsonHelper.getInt(response, "response_code") != 0) {
            logger.error("Failed to request trivia questions: Response code is not 0!\n" + response);
            return new Question[0];
        }

        JsonArray questionsJson = JsonHelper.getJsonArray(response, "results");
        Question[] questions = new Question[QUESTIONS_PER_GAME];
        for (int i = 0; i < QUESTIONS_PER_GAME; i++) {
            JsonObject questionJson = questionsJson.get(i).getAsJsonObject();
            Question question = Question.fromJson(questionJson, bot, gameId, i);
            questions[i] = question;
        }
        return questions;
    }

    private void initScores(Party party) {
        for (long userId : party.getMembersWithOwner()) {
            this.scores.put(userId, 0);
        }
    }

    public static MinigameTrivia start(Replyable replyable, CommandInfo commandInfo) {
        MinigameTrivia minigame = new MinigameTrivia(commandInfo, replyable);
        replyable.reply("You started a new game of trivia. The first question will be asked soon.");
        return minigame;
    }

    public static MinigameTrivia fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        long lastActive = JsonHelper.getLong(json, "active");

        JsonArray scoresJson = JsonHelper.getJsonArray(json, "scores");
        ArrayList<Pair<Long, Integer>> scores = new ArrayList<>(scoresJson.size());
        for (JsonElement jsonElement : scoresJson) {
            JsonObject scoreJson = jsonElement.getAsJsonObject();
            long userId = JsonHelper.getLong(scoreJson, "id");
            int score = JsonHelper.getInt(scoreJson, "score");
            scores.add(new Pair<>(userId, score));
        }

        int currentQuestionId = JsonHelper.getInt(json, "question_id");
        JsonArray questionsJson = JsonHelper.getJsonArray(json, "questions");
        Question[] questions = JsonHelper.jsonArrayToArray(questionsJson, jsonObject -> Question.fromJson(jsonObject, id), new Question[0]);

        return new MinigameTrivia(bot, id, scores, currentQuestionId, questions, lastActive);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("type", "trivia");
        json.addProperty("id", id);
        json.addProperty("active", lastActive);

        JsonArray scoresJson = new JsonArray();
        for (Map.Entry<Long, Integer> score : scores.entrySet()) {
            JsonObject scoreJson = new JsonObject();
            scoreJson.addProperty("id", score.getKey());
            scoreJson.addProperty("score", score.getValue());
            scoresJson.add(scoreJson);
        }
        json.add("scores", scoresJson);

        json.addProperty("question_id", currentQuestionId);
        json.add("questions", JsonHelper.arrayToJsonArray(questions));

        return json; // possu
    }

    /**
     * Asks the next question after given amount of seconds.
     */
    public void queueNextQuestion(int seconds, Replyable replyable, CommandInfo commandInfo) {
        Util.schedule(() -> askQuestion(replyable, commandInfo), seconds * 1000L);
    }

    public void askQuestion(Replyable replyable, CommandInfo commandInfo) {
        currentQuestionId++;
        questions[currentQuestionId].ask(replyable, bot, commandInfo.party(), commandInfo, this);
    }

    public void onAnswer(Party party, long userId, int answerId) {
        questions[currentQuestionId].onAnswer(bot, party, userId, answerId);
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean continueGame(Replyable replyable, CommandInfo commandInfo) {
        if (!this.paused) return false;
        this.paused = false;

        askQuestion(replyable, commandInfo);
        return true;
    }

    public void pause() {
        this.paused = true;
    }

    @Override
    public int getReward(Random random) {
        return bot.getRandom().nextInt(50 + 20 * getHighestScore(), 251);
    }

    public void addPoint(long userId) {
        scores.put(userId, scores.get(userId) + 1);
    }

    public int getScore(long userId) {
        return scores.get(userId);
    }

    public int getHighestScore() {
        int highest = 0;
        for (int score : scores.values()) {
            if (score > highest) highest = score;
        }
        return highest;
    }

    public String getScores() {
        StringBuilder scoresString = new StringBuilder();
        boolean newLine = false;

        final Stream<Map.Entry<Long, Integer>> sorted = scores.entrySet().stream().sorted(Comparator.comparing(t -> -t.getValue()));
        final Iterator<Map.Entry<Long, Integer>> iterator = sorted.iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Long, Integer> entry = iterator.next();
            if (newLine) scoresString.append('\n');
            else newLine = true;

            scoresString.append("<@").append(entry.getKey()).append(">: ").append(entry.getValue()).append(" points");
        }

        return scoresString.toString();
    }

    public MessageEmbed getScoresEmbed(SelfUser selfUser) {
        return new EmbedBuilder()
                .setTitle("TRIVIA RESULTS")
                .addField("Scores:", getScores(), false)
                .setColor(type.color)
                .setFooter(selfUser.getName(), selfUser.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    @Override
    public Long[] getWinners() {
        ArrayList<Long> winners = new ArrayList<>();
        int highest = 0;
        for (Map.Entry<Long, Integer> entry : scores.entrySet()) {
            int score = entry.getValue();
            if (score > highest) {
                highest = score;
                winners.clear();
                winners.add(entry.getKey());
            } else if (score == highest) {
                winners.add(entry.getKey());
            }
        }

        return winners.toArray(new Long[0]);
    }
}
