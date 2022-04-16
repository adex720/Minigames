package io.github.adex720.minigames.minigame.unscramble;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static MinigameUnscramble start(SlashCommandEvent event, CommandInfo ci) {
        MinigameUnscramble minigame = new MinigameUnscramble(ci);

        event.getHook().sendMessage("You started a new game of unscramble. The word is: " + minigame.wordKnown).queue();

        return minigame;
    }

    public static MinigameUnscramble start(ButtonClickEvent event, CommandInfo ci) {
        MinigameUnscramble minigame = new MinigameUnscramble(ci);

        event.reply("You started a new game of unscramble. The word is: " + minigame.wordKnown).queue();

        return minigame;
    }

    public void updateKnowWord() {
        int hints = 4 - life;

        String wordStart = "**" + word.substring(0, hints) + "**";

        int end = wordLength >= 8 ? 3 - life : 0;
        String wordShuffled = new String(shuffleLetters(word.substring(hints, wordLength - end)));
        String wordEnd = end > 0 ? "**" + word.substring(wordLength - end) + "**" : "";

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

    public void guess(SlashCommandEvent event, CommandInfo commandInfo) {
        active();
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
    public String quit() {
        super.quit();
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
