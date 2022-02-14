package io.github.adex720.minigames.minigame.higherlower;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MinigameHigherLower extends Minigame {

    private final int number;

    private int min;
    private int max;

    private int life;

    public MinigameHigherLower(MinigamesBot bot, long id, boolean isParty, long lastActive, int number, int min, int max, int life) {
        super(bot, bot.getMinigameTypeManager().HIGHER_OR_LOWER, id, isParty, lastActive);
        this.number = number;

        this.min = min;
        this.max = max;
        this.life = life;
    }

    public MinigameHigherLower(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getNumber(), 1, 60, 6);
    }

    public static MinigameHigherLower start(SlashCommandEvent event, CommandInfo ci) {
        MinigameHigherLower minigame = new MinigameHigherLower(ci);

        event.getHook().sendMessage("You started a new game of higher or lower. The range is: " + minigame.min + "-" + minigame.max).queue();

        return minigame;
    }

    public static MinigameHigherLower start(ButtonClickEvent event, CommandInfo ci) {
        MinigameHigherLower minigame = new MinigameHigherLower(ci);

        event.reply("You started a new game of higher or lower. The range is: " + minigame.min + "-" + minigame.max).queue();

        return minigame;
    }

    public void guess(SlashCommandEvent event) {
        active();
        int guess = (int) event.getOption("number").getAsDouble();

        if (guess == number) {
            event.getHook().sendMessage("Good job! " + number + " was the number!").queue();
            finish(event, true);
        } else {

            if (guess > max || guess < min) {
                event.getHook().sendMessage("That number is outside the range! The range is " + min + "-" + max + ".").queue();
                return;
            }

            life--;
            if (life > 0) {
                if (guess > number) {
                    max = guess - 1;
                    event.getHook().sendMessage(guess + " was too high. You have " + life + " tries left. The range is " + min + "-" + max + ".").queue();
                } else {
                    min = guess + 1;
                    event.getHook().sendMessage(guess + " was too low. You have " + life + " tries left The range is " + min + "-" + max + ".").queue();
                }
            } else {
                event.getHook().sendMessage("Wrong number. You ran out of tries. The number was " + number + ".").queue();
                finish(event, false);
            }
        }
    }

    @Override
    public String quit() {
        super.quit();
        return "You quit your previous game of higher or lower. The number was " + number + " and you had " + life + " life left.";
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "unscramble");

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

    public static int getNumber() {
        return ThreadLocalRandom.current().nextInt(60) + 1;
    }
}
