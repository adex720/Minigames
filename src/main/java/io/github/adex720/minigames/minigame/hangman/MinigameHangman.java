package io.github.adex720.minigames.minigame.hangman;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.Locale;

/**
 * See <a href="file:src/main/resources/stats.json">/resources/stopWords.txt</a> if you don't know how Hangman works.
 */
public class MinigameHangman extends Minigame {

    private final String word;
    private String wordGuessed;
    private int life;
    private final ArrayList<Character> guesses;

    // TODO: make replies embed and include image of hangman stage regarding to current life
    public MinigameHangman(MinigamesBot bot, long id, boolean isParty, long lastActive, String word, int life, ArrayList<Character> guesses) {
        super(bot, bot.getMinigameTypeManager().HANGMAN, id, isParty, lastActive);
        this.word = word;
        wordGuessed = "\\_".repeat(word.length());
        this.life = life;
        this.guesses = guesses;
    }

    public MinigameHangman(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getWord(ci.bot().getWordManager()), 10, new ArrayList<>());
    }

    public static MinigameHangman start(SlashCommandEvent event, CommandInfo ci) {
        MinigameHangman minigame = new MinigameHangman(ci);

        event.getHook().sendMessage("You started a new game of hangman. The word is: " + minigame.wordGuessed).queue();

        return minigame;
    }

    public static MinigameHangman start(ButtonClickEvent event, CommandInfo ci) {
        MinigameHangman minigame = new MinigameHangman(ci);

        event.reply("You started a new game of hangman. The word is: " + minigame.wordGuessed).queue();

        return minigame;
    }

    public void guess(SlashCommandEvent event, CommandInfo ci) {
        active();
        String guess = event.getOption("guess").getAsString();

        if (guess.length() == 1) {
            char guessLetter = guess.charAt(0);

            if (guessLetter >= 'A' && guessLetter <= 'Z') {
                guessLetter += 0x20; // Make guess lower case
            }

            if (guessLetter < 'a' || guessLetter > 'z') { // guess was not a letter
                event.getHook().sendMessage("You should only guess English letters!").queue();
                return;
            }

            if (guesses.contains(guessLetter)) {
                event.getHook().sendMessage("You have already guessed the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
                return;
            }

            int size = guesses.size();
            if (size == 0) {
                guesses.add(guessLetter);
            } else {
                for (int i = 0; i <= size; i++) { // Add guessed letter to guesses at alphabetical order
                    if (i == size) {
                        guesses.add(guessLetter);
                        break;
                    }

                    if (guesses.get(i) > guessLetter) {
                        guesses.add(i, guessLetter);
                        break;
                    }
                }
            }


            if (word.contains(guessLetter + "")) {

                StringBuilder wordBuilder = new StringBuilder();
                int length = word.length();

                boolean lettersLeft = false;

                for (int i = 0; i < length; i++) { // Update known word to match new guessed letter
                    char current = word.charAt(i);
                    if (guesses.contains(current)) {
                        wordBuilder.append(current);
                    } else {
                        wordBuilder.append("\\_");
                        lettersLeft = true;
                    }

                }

                if (lettersLeft) {
                    wordGuessed = wordBuilder.toString();
                    event.getHook().sendMessage("The word contains the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
                    return;
                }

                event.getHook().sendMessage("Good job! The word was " + word + ". You had " + life + " tries left!").queue();
                finish(event, ci, true);

            } else {
                life--;
                if (life == 0) {
                    event.getHook().sendMessage("You ran out of life. The word was " + word + ".").queue();
                    finish(event, ci, false);
                    return;
                }

                event.getHook().sendMessage("The word doesn't contain the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();

            }
        } else {
            if (word.equals(guess.toLowerCase(Locale.ROOT))) {
                event.getHook().sendMessage("Good Job! " + guess + " was the word! You had " + life + " health left.").queue();
                finish(event, ci, true);
                return;
            }

            life--;
            if (life == 0) {
                event.getHook().sendMessage("You ran out of life. The word was " + word + ".").queue();
                finish(event, ci, false);
                return;
            }

            if (Util.isUserNormal(guess)) {
                event.getHook().sendMessage(guess + " was not the word. You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
            } else {
                event.getHook().sendMessage("That was not the word! You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
            }
        }
    }

    @Override
    public String quit() {
        super.quit();
        return "You quit your previous game of hangman. The word was " + word + " and you had " + life + " health left.";
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "hangman");

        json.addProperty("id", id);
        json.addProperty("word", word);
        json.addProperty("life", life);

        json.addProperty("active", lastActive);
        json.addProperty("party", isParty);

        if (guesses.size() > 0) {
            JsonArray guessesJson = new JsonArray();
            guesses.forEach(guessesJson::add);

            json.add("guesses", guessesJson);
        }

        return json;
    }

    public static MinigameHangman fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        String word = JsonHelper.getString(json, "word");
        int life = JsonHelper.getInt(json, "life");

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        JsonArray guessesJson = JsonHelper.getJsonArray(json, "guesses", new JsonArray());
        ArrayList<Character> guesses = JsonHelper.JsonArrayToCharArrayList(guessesJson);

        return new MinigameHangman(bot, id, isParty, lastActive, word, life, guesses);
    }

    /**
     * @return Guessed letters on a new line. If no guesses are made yet the String will be empty.
     * */
    public String getGuesses() {
        if (guesses.isEmpty()) return "";

        StringBuilder builder = new StringBuilder("\nGuessed letters: ");
        boolean comma = false;

        for (char c : guesses) {
            if (comma) builder.append(", ");
            comma = true;
            builder.append(c);
        }
        return builder.toString();
    }

    public static String getWord(WordManager wordManager) {
        return wordManager.getWordForHangman();
    }

}
