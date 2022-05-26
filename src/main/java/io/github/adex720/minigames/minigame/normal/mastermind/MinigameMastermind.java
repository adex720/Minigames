package io.github.adex720.minigames.minigame.normal.mastermind;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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

    public static MinigameMastermind start(SlashCommandInteractionEvent event, CommandInfo ci) {
        MinigameMastermind minigame = new MinigameMastermind(ci);

        event.getHook().sendMessage("You started a new game of Mastermind! You have " + DEFAULT_GUESSES + " guesses left.").queue();

        return minigame;
    }

    public static MinigameMastermind start(ButtonInteractionEvent event, CommandInfo ci) {
        MinigameMastermind minigame = new MinigameMastermind(ci);

        event.reply("You started a new game of Mastermind! You have " + DEFAULT_GUESSES + " guesses left.").queue();

        return minigame;
    }

    /**
     * Generates a code tp guess in a game of Mastermind.
     */
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
        json.addProperty("type", "mastermind");
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
        return "You quit your previous game of mastermind. The code was " + codeToString(code) + " and you had " + (DEFAULT_GUESSES - guesses.size()) + " guesses left.";
    }


    /**
     * Places the given pointers on the board and sends correct messages.
     */
    public void place(SlashCommandInteractionEvent event, CommandInfo ci) {
        active(ci);

        int[] guessRaw = new int[]{event.getOption("first").getAsInt(),
                event.getOption("second").getAsInt(),
                event.getOption("third").getAsInt(),
                event.getOption("fourth").getAsInt()};

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

        if (triesLeft == 0) {
            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("MASTERMIND")
                    .addField("That wasn't the correct combination!", "You ran out of guesses!", true)
                    .addField("Board", guessesAndEmptiesToString(), true)
                    .addField("The combination was ", codeToString(code), true)
                    .setColor(type.color)
                    .setFooter(author.getName(), author.getAvatarUrl())
                    .setTimestamp(new Date().toInstant())
                    .build()).queue();

            finish(replyable, ci, false);
            return;
        }

        boolean duplicates = Util.hasPureDuplicateValues(guessRaw);

        String triesString = triesLeft == 1 ? "try" : "tries";

        String infoFieldDescription = "You have " + triesLeft + " " + triesString + " left";
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

    /**
     * Compacts the ids of the 4 guessed colors into one int.
     */
    public int compactCode(int first, int second, int third, int fourth) {
        return (first << 12) | (second << 8) | (third << 4) | fourth;
    }


    /**
     * Crates a String containing the emotes of already guessed codes and the hint pins.
     */
    public String guessesToString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean newLine = false;
        for (int guess : guesses) {
            if (newLine) {
                stringBuilder.append('\n');
            } else newLine = true;

            stringBuilder.append(codeToString(guess)).append(getHintEmote(guess));
        }

        return stringBuilder.toString();
    }

    /**
     * Crates a String containing the emotes of already guessed codes and the hint pins and
     * fills the remaining guesses with emptiness.
     * If the guesses ArrayList is empty the returned String starts with a new line.
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

        String emptyRow = "\n:black_circle::black_circle::black_circle::black_circle:<:mm_00:967107338538450984>";
        stringBuilder.append(emptyRow.repeat(DEFAULT_GUESSES - guesses.size()));

        return stringBuilder.toString();
    }

    /**
     * Returns the correct pin emotes for the given code.
     */
    public String codeToString(int code) {
        int first = (code >> 12) & 0xF;
        int second = (code >> 8) & 0xF;
        int third = (code >> 4) & 0xF;
        int fourth = code & 0xF;

        return castedType.COLOR_EMOTES[first] + castedType.COLOR_EMOTES[second] + castedType.COLOR_EMOTES[third] + castedType.COLOR_EMOTES[fourth];
    }

    /**
     * Returns the correct hint pin emote for the given guess code.
     */
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

        return bot.getEmote("mm_" + correct + wrong);
    }

    /**
     * Returns the amount of the given color on the given code.
     *
     * @param color color to check
     * @param code  code to check on
     * @return amount of correct color pins.
     */
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

    /**
     * Checks if the code contains a color.
     *
     * @param color color to find
     * @param code  code to check from
     * @return if the given code contains at least one pin of the given color
     */
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
