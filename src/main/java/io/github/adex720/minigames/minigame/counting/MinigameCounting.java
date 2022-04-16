package io.github.adex720.minigames.minigame.counting;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.minigame.party.PartyMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class MinigameCounting extends PartyMinigame { //TODO: make minigame end on too much waiting

    public static final int CORRECT_NUMBER = 1;
    public static final int WRONG_NUMBER = 2;
    public static final int IGNORE = 3;

    private final long channelId;

    private int count;

    public MinigameCounting(MinigamesBot bot, long gameId, long channelId, long lastActive) {
        super(bot, bot.getMinigameTypeManager().COUNTING, gameId, lastActive);
        this.channelId = channelId;

        count = 0;
    }

    public MinigameCounting(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.channelId(), System.currentTimeMillis());
    }

    public static MinigameCounting start(SlashCommandEvent event, CommandInfo ci) {
        MinigameCounting minigame = new MinigameCounting(ci);

        event.getHook().sendMessage("You started a new counting minigame.").queue();

        return minigame;
    }

    public static MinigameCounting start(ButtonClickEvent event, CommandInfo ci) {
        MinigameCounting minigame = new MinigameCounting(ci);

        event.getHook().sendMessage("You started a new counting minigame.").queue();

        return minigame;
    }


    @Override
    public JsonObject getAsJson() {
        JsonObject json = new  JsonObject();

        json.addProperty("id", id);
        json.addProperty("channel", channelId);
        json.addProperty("active", lastActive);

        return json;
    }

    public static MinigameCounting fromJson(MinigamesBot bot, JsonObject json) {
        long gameId = JsonHelper.getLong(json, "id");
        long channel = JsonHelper.getLong(json, "channel");
        long lastActive = JsonHelper.getLong(json, "active");

        return new MinigameCounting(bot, gameId, channel, lastActive);
    }

    public void onMessageReceive(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;

        String messageContent = event.getMessage().getContentRaw();

        if (messageContent.isEmpty()) return;

        active();
        String numberString = messageContent.split(" ")[0];
        int status = getStatus(numberString);

        CommandInfo commandInfo = CommandInfo.create(event, bot);
        if (status == CORRECT_NUMBER) {
            onCorrectNumber(event, commandInfo);
        } else if (status == WRONG_NUMBER) {
            Replyable replyable = Replyable.from(event);
            onWrongNumber(replyable, commandInfo);
        }
    }

    public int getStatus(String number) {
        try {
            int guess = Integer.parseInt(number);

            if (guess == count + 1) return CORRECT_NUMBER;
            return WRONG_NUMBER;
        } catch (Exception ignored) {
            return WRONG_NUMBER;
        }
    }

    public String getNext() {
        return Integer.toString(count + 1);
    }

    public void onCorrectNumber(MessageReceivedEvent event, CommandInfo commandInfo) {
        event.getMessage().addReaction("\u2705").queue();
        count++;
        commandInfo.profile().increaseStat("numbers counted");
    }

    public void onWrongNumber(Replyable replyable, CommandInfo commandInfo) {
        finish(replyable, commandInfo, false);
        replyable.reply("Wrong number! The ");
    }

    @Override
    public String addRewards(Replyable replyable, Profile profile, boolean won) {

        Random random = bot.getRandom();
        if (count >= 20 && random.nextInt(3) == 0) { // If won there's a 33% chance to get a crate.
            CrateType reward = random.nextBoolean() ? CrateType.COMMON : CrateType.UNCOMMON;
            profile.addCrate(reward);
            return "You received " + reward.getNameWithArticle() + " crate!";
        }

        int coins = Math.max(count, 200);

        profile.addCoins(coins, true, replyable);
        return "You received " + coins + " coins!";
    }

    @Override
    public String quit() {
        super.quit();
        return "You quit your counting game. You reached " + count + ".";
    }
}
