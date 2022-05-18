package io.github.adex720.minigames.minigame.normal.unscramble;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author adex720
 */
public class MinigameUnscramble extends Minigame {

    private final String word;
    private String wordKnown;
    private final int wordLength;
    private int life;

    public MinigameUnscramble(MinigamesBot bot, long id, boolean isParty, long lastActive, String word, int life) {
        super(bot, bot.getMinigameTypeManager().UNSCRAMBLE, id, isParty, lastActive);

        this.word = word;
        wordLength = word.length();
        this.life = life;
        updateKnowWord();
    }

    public MinigameUnscramble(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getWord(ci.bot().getWordManager()), 3);
    }

    public static MinigameUnscramble start(SlashCommandInteractionEvent event, CommandInfo ci) {
        MinigameUnscramble minigame = new MinigameUnscramble(ci);

        event.getHook().sendMessage("You started a new game of unscramble. The word is: " + minigame.wordKnown).queue();

        return minigame;
    }

    public static MinigameUnscramble start(ButtonInteractionEvent event, CommandInfo ci) {
        MinigameUnscramble minigame = new MinigameUnscramble(ci);

        event.reply("You started a new game of unscramble. The word is: " + minigame.wordKnown).queue();

        return minigame;
    }

    @Override
    public int getReward(Random random) {
        return switch (life) {
            case 1 -> random.nextInt(100, 151);
            case 2 -> random.nextInt(150, 201);
            case 3 -> random.nextInt(200, 251);
            default -> throw new IllegalStateException("Unexpected value for guesses left: " + life);
        };
    }

    public void updateKnowWord() {
        int hints = 4 - life;

        String wordStart = "**__" + word.substring(0, hints) + "__**";

        int end = wordLength >= 8 ? 3 - life : 0;
        String wordShuffled = new String(shuffleLetters(word.substring(hints, wordLength - end)));
        String wordEnd = end > 0 ? "**__" + word.substring(wordLength - end) + "__**" : "";

        wordKnown = wordStart + wordShuffled + wordEnd;
    }

    public char[] shuffleLetters(String original) {
        List<String> letters = Arrays.asList(original.split(""));
        Collections.shuffle(letters);
        char[] shuffled = new char[original.length()];

        for (int i = 0; i < original.length(); i++) {
            shuffled[i] = letters.get(i).charAt(0);
        }

        return shuffled;
    }

    public void guess(SlashCommandInteractionEvent event, CommandInfo commandInfo) {
        active(commandInfo);
        String guess = event.getOption("word").getAsString();
        Replyable replyable = Replyable.from(event);

        if (guess.equals(word)) {
            replyable.reply("Good job! " + word + " was the word.");
            finish(replyable, commandInfo, true);
            return;
        }

        life--;

        if (life > 0) {
            updateKnowWord();
            if (Util.isUserNormal(guess)) {
                replyable.reply(guess + " was not the word. You have " + life + " guesses left. The word is " + wordKnown);
            } else {
                replyable.reply("That was not the word. The word is " + wordKnown);
            }

            return;
        }

        if (Util.isUserNormal(guess)) {
            replyable.reply(guess + " was not the word. You ran out of life. The word was " + word + ".");
        } else {
            replyable.reply("That was not the word. You ran out of life. The word was " + word + ".");
        }

        finish(replyable, commandInfo, false);
    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of unscramble. The word was " + word + " and you had " + life + " health left.";
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "unscramble");

        json.addProperty("id", id);
        json.addProperty("word", word);
        json.addProperty("life", life);

        json.addProperty("active", lastActive);
        json.addProperty("party", isParty);

        return json;
    }

    public static MinigameUnscramble fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        String word = JsonHelper.getString(json, "word");
        int life = JsonHelper.getInt(json, "life");

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        return new MinigameUnscramble(bot, id, isParty, lastActive, word, life);
    }

    public static String getWord(WordManager wordManager) {
        return wordManager.getWordForUnscramble();
    }

}
