package io.github.adex720.minigames.minigame.hangman;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class MinigameHangman extends Minigame {

    private final String word;
    private String wordGuessed;
    private int life;
    private final ArrayList<Character> guesses;

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

    public void guess(SlashCommandEvent event) {
        String guess = event.getOption("guess").getAsString();

        if (guess.length() == 1) {
            char guessLetter = guess.charAt(0);

            if (guessLetter >= 'A' && guessLetter <= 'Z') {
                guessLetter += 0x20;
            }

            if (guessLetter < 'a' || guessLetter > 'z') {
                event.getHook().sendMessage("You should only guess English letters!").queue();
                return;
            }

            if (!guesses.contains(guessLetter)) {

                int size = guesses.size();
                if (size == 0) {
                    guesses.add(guessLetter);
                } else {
                    for (int i = 0; i <= size; i++) {
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
                        event.getHook().sendMessage("The word contains the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
                        return;
                    }

                    event.getHook().sendMessage("Good job! The word was " + word + ". You had " + life + " tries left!").queue();
                    finish(event, true);

                } else {
                    life--;
                    if (life == 0) {
                        event.getHook().sendMessage("You ran out of life. The word was " + word + ".").queue();
                        finish(event, false);
                        return;
                    }

                    event.getHook().sendMessage("The word doesn't contain the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
                }


            } else {
                event.getHook().sendMessage("You have already guessed the letter " + guessLetter + ". You have " + life + " health left. The word is: " + wordGuessed + getGuesses()).queue();
            }

        } else {
            if (word.equals(guess.toLowerCase(Locale.ROOT))) {
                event.getHook().sendMessage("Good Job! " + guess + " was the word! You had " + life + " health left.").queue();
                finish(event, true);
                return;
            }

            life--;
            if (life == 0) {
                event.getHook().sendMessage("You ran out of life. The word was " + word + ".").queue();
                finish(event, false);
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

        ArrayList<Character> guesses = new ArrayList<>();
        for (JsonElement guess : JsonHelper.getJsonArray(json, "guesses", new JsonArray())) {
            guesses.add((char) guess.getAsByte());
        }

        return new MinigameHangman(bot, id, isParty, lastActive, word, life, guesses);
    }

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
