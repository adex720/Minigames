package io.github.adex720.minigames.minigame.normal.higherlower;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author adex720
 */
public class MinigameHigherLower extends Minigame {

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 60;
    public static final int DEFAULT_GUESSES = 6;

    private final int number;

    private int min;
    private int max;

    private int life;

    /**
     * @param number number to guess
     * @param min    smallest potential answer
     * @param max    largest potential answer
     * @param life   guesses left
     */
    public MinigameHigherLower(MinigamesBot bot, long id, boolean isParty, long lastActive, int number, int min, int max, int life) {
        super(bot, bot.getMinigameTypeManager().HIGHER_OR_LOWER, id, isParty, lastActive);
        this.number = number;

        this.min = min;
        this.max = max;
        this.life = life;
    }

    public MinigameHigherLower(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getNumber(ci.bot()), MIN_VALUE, MAX_VALUE, DEFAULT_GUESSES);
    }

    public static MinigameHigherLower start(Replyable replyable, CommandInfo ci) {
        MinigameHigherLower minigame = new MinigameHigherLower(ci);
        replyable.reply("You started a new game of higher or lower. The range is: " + minigame.min + "-" + minigame.max);
        return minigame;
    }

    @Override
    public int getReward(Random random) {
        if (life == 1) return random.nextInt(100, 251);
        if (life == 2) return random.nextInt(200, 251);
        return random.nextInt(230, 251);
    }

    public void guess(SlashCommandInteractionEvent event, CommandInfo commandInfo) {
        active(commandInfo);
        int guess = event.getOption("number").getAsInt();
        Replyable replyable = Replyable.from(event);

        if (guess == number) {
            replyable.reply("Good job! " + number + " was the number!");
            finish(replyable, commandInfo, true);
            return;
        }

        if (guess > max || guess < min) { // No harm being friendly
            replyable.reply("That number is outside the range! The range is " + min + "-" + max + ".");
            return;
        }

        life--;
        if (life == 0) {
            replyable.reply("Wrong number. You ran out of tries. The number was " + number + ".");
            finish(replyable, commandInfo, false);
            return;
        }

        String guessWithProperForm = life == 1 ? " guess" : " guesses";
        if (guess > number) { // Send new range
            max = guess - 1;
            replyable.reply(guess + " was too high. You have " + life + guessWithProperForm + " left. The range is " + min + "-" + max + ".");
        } else {
            min = guess + 1;
            replyable.reply(guess + " was too low. You have " + life + guessWithProperForm + " left The range is " + min + "-" + max + ".");
        }

    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of higher or lower. The number was " + number + " and you had " + life + " guess" + (life > 1 ? "es" : "") + " left.";
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "higher-lower");

        json.addProperty("id", id);
        json.addProperty("number", number);
        json.addProperty("min", min);
        json.addProperty("max", max);
        json.addProperty("life", life);

        json.addProperty("active", lastActive);
        json.addProperty("party", isParty);

        return json;
    }

    public static MinigameHigherLower fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        int number = JsonHelper.getInt(json, "number");
        int life = JsonHelper.getInt(json, "life");

        int min = JsonHelper.getInt(json, "min");
        int max = JsonHelper.getInt(json, "max");

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        return new MinigameHigherLower(bot, id, isParty, lastActive, number, min, max, life);
    }

    public static int getNumber(MinigamesBot bot) {
        return bot.getRandom().nextInt(60) + 1;
    }
}
