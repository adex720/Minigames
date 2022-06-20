package io.github.adex720.minigames.minigame.party.trivia;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * @author adex720
 */
public class Question implements JsonSavable {

    public final String question;
    private final String correctAnswer;
    private final String[] incorrectAnswers;
    private final int optionsShuffleSeed;

    public final long gameId;
    public final int questionId;

    private final ArrayList<Long> answered; // Users who have answered
    private final ArrayList<Long> correctAnswered; // Users who have answered correctly
    private long endTime;

    private Replyable replyable;

    public Question(String question, String correctAnswer, String[] incorrectAnswers, long gameId, int questionId, Random random) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;

        this.gameId = gameId;
        this.questionId = questionId;

        // 10111 = 0x17
        // The seed contains 3 parts
        // 10 1 11
        // The first part determines the index of the first incorrect answer when skipping the correct answer.
        // The second part determines the index of the second incorrect answer when skipping the correct and first incorrect answer.
        // The last parts determines the index of the correct answer.
        optionsShuffleSeed = random.nextInt(0x18); // Bound is excluded

        answered = new ArrayList<>();
        correctAnswered = new ArrayList<>();
        replyable = null;
    }

    public Question(String question, String correctAnswer, String[] incorrectAnswers, long gameId, int questionId, int optionsShuffleSeed) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;

        this.gameId = gameId;
        this.questionId = questionId;

        this.optionsShuffleSeed = optionsShuffleSeed;

        answered = new ArrayList<>();
        correctAnswered = new ArrayList<>();
        replyable = null;
    }

    /**
     * Creates a question from a json file saved by this program.
     */
    public static Question fromJson(JsonObject json, long gameId) {
        int questionId = JsonHelper.getInt(json, "id");
        String question = JsonHelper.getString(json, "question");
        String correctAnswer = JsonHelper.getString(json, "correct_answer");

        JsonArray incorrectAnswersJson = JsonHelper.getJsonArray(json, "incorrect_answers");
        int incorrectAnswersCount = incorrectAnswersJson.size();
        String[] incorrectAnswers = new String[incorrectAnswersCount];
        for (int i = 0; i < incorrectAnswersCount; i++) {
            incorrectAnswers[i] = incorrectAnswersJson.get(i).getAsString();
        }

        int seed = JsonHelper.getInt(json, "seed");
        return new Question(question, correctAnswer, incorrectAnswers, gameId, questionId, seed);
    }

    /**
     * Creates a question from a json file received from the trivia api.
     */
    public static Question fromJson(JsonObject json, MinigamesBot bot, long gameId, int questionId) {
        String question = Util.format(JsonHelper.getString(json, "question"));
        String correctAnswer = Util.format(JsonHelper.getString(json, "correct_answer"));

        JsonArray incorrectAnswersJson = JsonHelper.getJsonArray(json, "incorrect_answers");
        int incorrectAnswersCount = incorrectAnswersJson.size();
        String[] incorrectAnswers = new String[incorrectAnswersCount];
        for (int i = 0; i < incorrectAnswersCount; i++) {
            incorrectAnswers[i] = Util.format(incorrectAnswersJson.get(i).getAsString());
        }

        return new Question(question, correctAnswer, incorrectAnswers, gameId, questionId, bot.getRandom());
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("id", questionId);
        json.addProperty("question", question);
        json.addProperty("correct_answer", correctAnswer);

        JsonArray incorrectAnswersJson = JsonHelper.arrayToJsonArray(incorrectAnswers);
        json.add("incorrect_answers", incorrectAnswersJson);

        json.addProperty("seed", optionsShuffleSeed);

        return json;
    }

    public void ask(Replyable replyable, MinigamesBot bot, Party party, CommandInfo commandInfo, MinigameTrivia minigame) {
        endTime = System.currentTimeMillis() + MinigameTrivia.TIME_TO_ANSWER;
        MessageEmbed embed = getEmbed(bot, party);
        replyable.reply(embed, getButtons());
        this.replyable = replyable;

        Util.schedule(() -> finishQuestion(bot, party, commandInfo, minigame), MinigameTrivia.TIME_TO_ANSWER);
    }

    private void finishQuestion(MinigamesBot bot, Party party, CommandInfo commandInfo, MinigameTrivia minigame) {
        disableButtons(replyable.getLastMessage(), bot, party);
        calculatePoints(minigame, commandInfo);
    }

    public void updateMessage(MinigamesBot bot, Party party) {
        User user = bot.getJda().getSelfUser();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("TRIVIA " + questionId + "/" + MinigameTrivia.QUESTIONS_PER_GAME)
                .addField(question, getOptions(), false)
                .addField("Answers given: " + answered.size() + "/" + party.size(), "Time ends in " + getSecondsLeft() + " seconds.", false)
                .setColor(0)
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
        this.replyable.waitLastMessage(2).editMessageEmbeds(embed).setActionRows(getButtons()).queue();
    }

    public void calculatePoints(MinigameTrivia minigame, CommandInfo commandInfo) {
        updateTriviaScores(minigame);

        String correctAnswerers = getCorrectAnsweredUsersAsMentions();
        String message = "The correct answer was " + correctAnswer + " (" + getCorrectAnswerLetter() + ")!\n" + correctAnswerers + " knew the correct answer.";

        if (questionId + 1 == MinigameTrivia.QUESTIONS_PER_GAME) {
            finishTrivia(minigame, commandInfo, message);
            return;
        }

        prepareNextQuestion(minigame, commandInfo, message);
    }

    private void finishTrivia(MinigameTrivia minigame, CommandInfo commandInfo, String lastRoundMessage) {
        finishMinigame(minigame, commandInfo);
        replyable.reply(lastRoundMessage);
        replyable.reply(minigame.getScoresEmbed(commandInfo.bot().getJda().getSelfUser()));
    }

    private void prepareNextQuestion(MinigameTrivia minigame, CommandInfo commandInfo, String lastRoundMessage) {
        if (minigame.isPaused()) {
            replyable.reply(lastRoundMessage + "\nContinue the game with `/trivia continue`.");
            answered.clear();
            correctAnswered.clear();
            return;
        }

        replyable.reply(lastRoundMessage + "\nThe next question will be asked soon.");
        minigame.queueNextQuestion(5, this.replyable, commandInfo);

        answered.clear();
        correctAnswered.clear();
    }

    private void updateTriviaScores(MinigameTrivia minigame) {
        correctAnswered.forEach(minigame::addPoint);
    }

    /**
     * Unloads the minigame from {@link io.github.adex720.minigames.gameplay.manager.minigame.MinigameManager}.
     */
    private void finishMinigame(MinigameTrivia minigame, CommandInfo commandInfo) {
        minigame.finish(replyable, commandInfo, true);
    }

    public MessageEmbed getEmbed(MinigamesBot bot, Party party) {
        User user = bot.getJda().getSelfUser();
        return new EmbedBuilder()
                .setTitle("TRIVIA " + questionId + "/" + MinigameTrivia.QUESTIONS_PER_GAME)
                .addField(question, getOptions(), false)
                .addField("Answers given: " + answered.size() + "/" + party.size(), "Time ends in " + getSecondsLeft() + " seconds.", false)
                .setColor(0)
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    public ActionRow getButtons() {
        return ActionRow.of(
                getButton(0),
                getButton(1),
                getButton(2),
                getButton(3));
    }

    public ActionRow getButtonsDisabled() {
        return ActionRow.of(
                getButton(0).asDisabled(),
                getButton(1).asDisabled(),
                getButton(2).asDisabled(),
                getButton(3).asDisabled());
    }

    public Button getButton(int index) {
        return Button.primary("trivia-" + gameId + "-" + index, (char) ('A' + index) + "");
    }

    /**
     * Returns the amount of seconds left to answer.
     * The result is rounded to the closest integer.
     */
    public int getSecondsLeft() {
        return (int) (endTime - System.currentTimeMillis() + 500) / 1000;
    }

    /**
     * Returns the options with each on being on their own line and
     * the correct answer being on index {@link Question#optionsShuffleSeed} while the top most option has id 0.
     */
    public String getOptions() {
        int correctAnswerIndex = optionsShuffleSeed & 0x3;

        int firstIncorrectAnswerIndex = (optionsShuffleSeed >> 3) & 0x3;
        int secondIncorrectAnswerIndex = (optionsShuffleSeed >> 2) & 0x1;
        int thirdIncorrectAnswerIndex = 0;

        if (secondIncorrectAnswerIndex >= firstIncorrectAnswerIndex) secondIncorrectAnswerIndex++;

        if (thirdIncorrectAnswerIndex >= firstIncorrectAnswerIndex) thirdIncorrectAnswerIndex++;
        if (thirdIncorrectAnswerIndex >= secondIncorrectAnswerIndex) {
            thirdIncorrectAnswerIndex++;
            if (thirdIncorrectAnswerIndex == firstIncorrectAnswerIndex) thirdIncorrectAnswerIndex++;
        }

        if (firstIncorrectAnswerIndex >= correctAnswerIndex) firstIncorrectAnswerIndex++;
        if (secondIncorrectAnswerIndex >= correctAnswerIndex) secondIncorrectAnswerIndex++;
        if (thirdIncorrectAnswerIndex >= correctAnswerIndex) thirdIncorrectAnswerIndex++;

        // Adding options to array
        String[] options = new String[4];
        options[correctAnswerIndex] = correctAnswer;
        options[firstIncorrectAnswerIndex] = incorrectAnswers[0];
        options[secondIncorrectAnswerIndex] = incorrectAnswers[1];
        options[thirdIncorrectAnswerIndex] = incorrectAnswers[2];

        return "A: " + options[0] + "\nB: " + options[1] + "\nC: " + options[2] + "\nD: " + options[3];
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getCorrectAnswerIndex() {
        return optionsShuffleSeed & 0x3;
    }

    public char getCorrectAnswerLetter() {
        return (char) ((optionsShuffleSeed & 0x3) + 'A');
    }

    /**
     * Returns list of all users who answered the correct answer to the previous question.
     * The format is: "<@1234>, <@1235>, <@1236> and <@1237>".
     * If 0 users gave the correct answer "Nobody" is returned.
     */
    public String getCorrectAnsweredUsersAsMentions() {
        int amount = correctAnswered.size();
        if (amount == 0) return "Nobody";
        if (amount == 1) return "<@" + correctAnswered.get(0) + ">";

        StringBuilder correctMentions = new StringBuilder(); // Mention of users who answered the correct answer.
        boolean comma = false;
        int lastId = amount - 1;

        for (int i = 0; i < lastId; i++) {
            long userId = correctAnswered.get(i);
            if (comma) correctMentions.append(", ");
            else comma = true;

            correctMentions.append("<@").append(userId).append(">");
        }

        correctMentions.append(" and <@").append(correctAnswered.get(lastId)).append(">");
        return correctMentions.toString();
    }

    public void onAnswer(MinigamesBot bot, Party party, long userId, int answerId) {
        if (answered.contains(userId)) return;

        answered.add(userId);
        if (answerId == getCorrectAnswerIndex()) correctAnswered.add(userId);
        updateMessage(bot, party);
    }

    private void disableButtons(Message message, MinigamesBot bot, Party party) {
        if (message == null) return;
        message.editMessageEmbeds(getEmbed(bot, party)).setActionRows(getButtonsDisabled()).queue();
    }
}
