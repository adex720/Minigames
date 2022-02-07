package io.github.adex720.minigames.minigame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MinigameHangman extends Minigame {

    private final String word;
    private String wordGuessed;
    private int life;
    private final Set<Character> guesses;

    public MinigameHangman(MinigamesBot bot, long id, boolean isParty, long lastActive, String word, int life, Set<Character> guesses) {
        super(bot, bot.getMinigameTypeManager().HANGMAN, id, isParty, lastActive);
        this.word = word;
        wordGuessed = "\\_".repeat(word.length());
        this.life = life;
        this.guesses = guesses;
    }

    public MinigameHangman(CommandInfo ci) {
        this(ci.bot(), ci.authorId(), ci.isInParty(), System.currentTimeMillis(), getWord(), 10, new HashSet<>());
    }

    public static MinigameHangman start(SlashCommandEvent event, CommandInfo ci) {
        MinigameHangman minigame = new MinigameHangman(ci);

        event.reply("You started a new game of hangman. The word is: " + minigame.wordGuessed).queue();

        return minigame;
    }

    public void guess(SlashCommandEvent event, CommandInfo ci) {
        String guess = event.getOption("guess").getAsString();

        if (guess.length() == 1) {
            char guessLetter = guess.charAt(0);

            if (guessLetter >= 'A' && guessLetter <= 'Z') {
                guessLetter -= 0x20;
            }

            if (guesses.add(guessLetter)) {
                if (word.contains(guessLetter + "")) {
                    guesses.add(guessLetter);

                    StringBuilder wordBuilder = new StringBuilder();
                    int length = word.length();

                    boolean lettersLeft = false;

                    for (int i = 0; i < length; i++) {
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
                        event.reply("The word contains the letter " + guessLetter + ". The word is: " + wordGuessed).queue();
                        return;
                    }

                    event.reply("Good job! The word was " + word + ".").queue();
                    finish(true);
                    return;

                } else {
                    event.reply("The word doesn't contain the letter " + guessLetter + ". The word is: " + wordGuessed).queue();
                }


            } else {
                event.reply("You have already guessed the letter. The word is: " + wordGuessed).queue();
                return;
            }

        } else {
            if (word.equals(guess.toLowerCase(Locale.ROOT))) {
                event.reply("Good Job! " + guess + " was the word").queue();
                finish(true);
                return;
            }

        }

        life--;
        if (life == 0) {
            event.reply("You ran out of life. The word was " + word + ".").queue();
            finish(false);
        }

    }


    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

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

        return null;
    }

    public static MinigameHangman fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        String word = JsonHelper.getString(json, "word");
        int life = JsonHelper.getInt(json, "life");

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        Set<Character> guesses = new HashSet<>();
        for (JsonElement guess : JsonHelper.getJsonArray(json, "guesses", new JsonArray())) {
            guesses.add((char) guess.getAsByte());
        }

        return new MinigameHangman(bot, id, isParty, lastActive, word, life, guesses);
    }

    public static String getWord() {
        return "Discord"; // TODO: randomize word
    }


}
