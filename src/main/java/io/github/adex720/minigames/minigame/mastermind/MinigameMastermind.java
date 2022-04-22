package io.github.adex720.minigames.minigame.mastermind;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Only supports the 'easy' mode meaning no empty spots or duplicate colors exist.
 *
 * @author adex720
 */
public class MinigameMastermind extends Minigame {

    public static final int DEFAULT_GUESSES = 10;

    private final int code;
    private final ArrayList<Integer> guesses;

    private final MinigameTypeMastermind castedType;

    public MinigameMastermind(MinigamesBot bot, long id, boolean isParty, long lastActive, int code, ArrayList<Integer> guesses) {
        super(bot, bot.getMinigameTypeManager().MASTERMIND, id, isParty, lastActive);
        castedType = (MinigameTypeMastermind) bot.getMinigameTypeManager().MASTERMIND;

        this.code = code;
        this.guesses = guesses;
    }

    public MinigameMastermind(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getCode(ci.bot()), new ArrayList<>());
    }

    public static MinigameMastermind start(SlashCommandEvent event, CommandInfo ci) {
        MinigameMastermind minigame = new MinigameMastermind(ci);

        event.getHook().sendMessage("You started a new game of Mastermind! You have " + DEFAULT_GUESSES + " guesses left.").queue();

        return minigame;
    }

    public static MinigameMastermind start(ButtonClickEvent event, CommandInfo ci) {
        MinigameMastermind minigame = new MinigameMastermind(ci);

        event.reply("You started a new game of Mastermind! You have " + DEFAULT_GUESSES + " guesses left.").queue();

        return minigame;
    }

    public static int getCode(MinigamesBot bot) {
        Random random = bot.getRandom();

        // get the first color
        int first = random.nextInt(8);

        // get the second color and ensure it's different
        // because max 7 is added it can't be the same
        int second = (first + random.nextInt(7) + 1) % 8;


        // get the third color and ensure it's different
        // because max 6 is added it can't be the same or one smaller than second
        int third = (second + random.nextInt(6) + 1) % 8;
        if (third == first) {
            if (third < 7) third++;
            else third = 0;
        }

        // get the third color and ensure it's different
        // because max 5 is added it can't be the same or one or two smaller than third
        int fourth = (third + random.nextInt(5) + 1) % 8;
        if (fourth == first || fourth == second) {
            if (fourth < 7) fourth++;
            else fourth = 0;

            if (fourth == second || fourth == first) { // first and second are duplicates
                if (fourth < 7) fourth++;
                else fourth = 0;
            }
        }

        return (first << 12) | (second << 8) | (third << 4) | fourth; // Compact result
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "unscramble");
        json.addProperty("id", id);

        json.addProperty("code", code);
        json.add("guesses", JsonHelper.arrayListIntToJsonArray(guesses));

        json.addProperty("active", lastActive);
        json.addProperty("party", isParty);

        return json;
    }

    public static MinigameMastermind fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");

        int code = JsonHelper.getInt(json, "code");
        ArrayList<Integer> guesses = JsonHelper.jsonArrayToIntArrayList(JsonHelper.getJsonArray(json, "guesses"));

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        return new MinigameMastermind(bot, id, isParty, lastActive, code, guesses);
    }

    @Override
    public int getReward(Random random) {
        int guessesAmount = guesses.size();

        if (guessesAmount <= 3) return 250;

        int min = 100 + 10 * (DEFAULT_GUESSES - guessesAmount);
        int max = min + 71;
        return random.nextInt(min, max);
    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of unscramble. The code was " + code + " and you had " + (DEFAULT_GUESSES - guesses.size()) + " guesses left.";
    }


    public void place(SlashCommandEvent event, CommandInfo ci) {
        int[] guessRaw = new int[]{(int) event.getOption("first").getAsLong(),
                (int) event.getOption("second").getAsLong(),
                (int) event.getOption("third").getAsLong(),
                (int) event.getOption("fourth").getAsLong()};

        int guessCompacted = compactCode(guessRaw[0], guessRaw[1], guessRaw[2], guessRaw[3]);

        guesses.add(guessCompacted);

        User author = ci.author();
        Replyable replyable = Replyable.from(event);
        if (guessCompacted == code) {
            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("MASTERMIND")
                    .addField("You guessed the correct combination!", finish(replyable, ci, true), true)
                    .addField("Board", guessesAndEmptiesToString(), true)
                    .setColor(type.color)
                    .setFooter(author.getName(), author.getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build()).queue();
            return;
        }

        int triesLeft = DEFAULT_GUESSES - guesses.size();

        boolean duplicates = Util.hasPureDuplicateValues(guessRaw);
        String infoFieldDescription = "You have " + triesLeft + " tries left";
        if (duplicates) {
            infoFieldDescription += "\n**Hint:** The game plays on the easier difficulty and does not contain empty spots or duplicate colors.";
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("MASTERMIND")
                .addField("That wasn't the correct combination!", infoFieldDescription, true)
                .addField("Board", guessesAndEmptiesToString(), true)
                .setColor(type.color)
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
    }


    public int compactCode(int first, int second, int third, int fourth) {
        return (first << 12) | (second << 8) | (third << 4) | fourth;
    }

    public String guessesToString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean newLine = false;
        for (int guess : guesses) {
            if (newLine) {
                stringBuilder.append('\n');
            } else newLine = true;

            stringBuilder.append(codeToString(guess));
        }

        return stringBuilder.toString();
    }

    /**
     * Doesn't work when guesses ArrayList is empty.
     */
    public String guessesAndEmptiesToString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean newLine = false;
        for (int guess : guesses) {
            if (newLine) {
                stringBuilder.append('\n');
            } else newLine = true;

            stringBuilder.append(codeToString(guess)).append(getHintEmote(guess));
        }

        String emptyRow = "\n:black_circle::black_circle::black_circle::black_circle:<:mastermind_00:967107338538450984>";
        stringBuilder.append(emptyRow.repeat(DEFAULT_GUESSES - guesses.size()));

        return stringBuilder.toString();
    }

    public String codeToString(int code) {
        int first = (code >> 12) & 0xF;
        int second = (code >> 8) & 0xF;
        int third = (code >> 4) & 0xF;
        int fourth = code & 0xF;

        return castedType.COLOR_EMOTES[first] + castedType.COLOR_EMOTES[second] + castedType.COLOR_EMOTES[third] + castedType.COLOR_EMOTES[fourth];
    }

    public String getHintEmote(int guess) {
        int codeFirst = (code >> 12) & 0xF;
        int codeSecond = (code >> 8) & 0xF;
        int codeThird = (code >> 4) & 0xF;
        int codeFourth = code & 0xF;

        int guessFirst = (guess >> 12) & 0xF;
        int guessSecond = (guess >> 8) & 0xF;
        int guessThird = (guess >> 4) & 0xF;
        int guessFourth = guess & 0xF;


        int correct = 0;
        if (codeFirst == guessFirst) correct++;
        if (codeSecond == guessSecond) correct++;
        if (codeThird == guessThird) correct++;
        if (codeFourth == guessFourth) correct++;

        int wrongPlaces = 0;

        if (hasColor(codeFirst, guess)) wrongPlaces++;
        if (hasColor(codeSecond, guess)) wrongPlaces++;
        if (hasColor(codeThird, guess)) wrongPlaces++;
        if (hasColor(codeFourth, guess)) wrongPlaces++;

        int wrong = wrongPlaces - correct;

        return bot.getEmote("mastermind_" + correct + wrong);
    }

    public int amountOfColor(int color, int code) {
        int codeFirst = (code >> 12) & 0xF;
        int codeSecond = (code >> 8) & 0xF;
        int codeThird = (code >> 4) & 0xF;
        int codeFourth = code & 0xF;

        int amount = 0;
        if (color == codeFirst) amount++;
        if (color == codeSecond) amount++;
        if (color == codeThird) amount++;
        if (color == codeFourth) amount++;

        return amount;
    }

    public boolean hasColor(int color, int code) {
        int codeFirst = (code >> 12) & 0xF;
        if (color == codeFirst) return true;

        int codeSecond = (code >> 8) & 0xF;
        if (color == codeSecond) return true;

        int codeThird = (code >> 4) & 0xF;
        if (color == codeThird) return true;

        int codeFourth = code & 0xF;
        return color == codeFourth;
    }

}
