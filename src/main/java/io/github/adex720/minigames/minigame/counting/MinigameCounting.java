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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Locale;
import java.util.Random;

public class MinigameCounting extends PartyMinigame { //TODO: make minigame end on too much waiting

    public static final int CORRECT_NUMBER = 1;
    public static final int WRONG_NUMBER = 2;
    public static final int SAME_USER = 3;
    public static final int IGNORE = 4;

    public static final Mode MODE_BASE_10 = new Mode() {
        @Override
        public String getValue(int value) {
            return Integer.toString(value);
        }
    };

    public static final Mode MODE_HEXADECIMAL = new Mode() {
        @Override
        public String getValue(int value) {
            return Integer.toString(value, 16);
        }
    };

    public static final Mode MODE_BINARY = new Mode() {
        @Override
        public String getValue(int value) {
            return Integer.toString(value, 2);
        }
    };

    public static final Mode MODE_LETTERS = new Mode() {
        @Override
        public String getValue(int value) {
            StringBuilder letters = new StringBuilder();
            String hexavigesimal = Integer.toString(value, 26); // get on format like hexadecimal but continuing P (16th letter)

            for (int i = 0; i < hexavigesimal.length(); i++) {
                char c = hexavigesimal.charAt(i);

                if (c <= '9') { // 0 - 9
                    letters.append((char) (c + 0x30)); // 1 -> A, etc.
                } else { // A - Q
                    letters.append(c);
                }
            }

            return letters.toString();
        }
    };

    public static final int MODE_BASE_10_ID = 1;
    public static final int MODE_HEXADECIMAL_ID = 2;
    public static final int MODE_BINARY_ID = 3;
    public static final int MODE_LETTERS_ID = 4;


    public static final String CORRECT_NUMBER_EMOTE = "\u2705";

    private final long channelId;

    private final int modeId;

    private long lastUser;
    private int count;

    public MinigameCounting(MinigamesBot bot, long gameId, long channelId, int modeId, long lastActive) {
        super(bot, bot.getMinigameTypeManager().COUNTING, gameId, lastActive);
        this.channelId = channelId;

        this.modeId = modeId;
        count = 0;
        lastUser = 0;
    }

    public MinigameCounting(MinigamesBot bot, long gameId, long channelId, int modeId, int count, long lastUser, long lastActive) {
        super(bot, bot.getMinigameTypeManager().COUNTING, gameId, lastActive);
        this.channelId = channelId;

        this.modeId = modeId;
        this.count = count;
        this.lastUser = lastUser;
    }

    public MinigameCounting(CommandInfo ci, int mode) {
        this(ci.bot(), ci.gameId(), ci.channelId(), mode, System.currentTimeMillis());
    }

    public static MinigameCounting start(SlashCommandEvent event, CommandInfo ci) {
        OptionMapping optionMapping = event.getOption("mode");
        int mode = optionMapping != null ? ((int) event.getOption("mode").getAsLong()) : MODE_BASE_10_ID;

        MinigameCounting minigame = new MinigameCounting(ci, mode);

        event.getHook().sendMessage("You started a new counting minigame.").queue();

        return minigame;
    }

    public static MinigameCounting start(ButtonClickEvent event, CommandInfo ci) {
        MinigameCounting minigame = new MinigameCounting(ci, MODE_BASE_10_ID); // TODO: save mode

        event.getHook().sendMessage("You started a new counting minigame.").queue();

        return minigame;
    }


    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("id", id);
        json.addProperty("active", lastActive);

        json.addProperty("channel", channelId);
        json.addProperty("mode", modeId);

        json.addProperty("count", count);
        json.addProperty("last", lastUser); // Last user who counted

        return json;
    }

    public static MinigameCounting fromJson(MinigamesBot bot, JsonObject json) {
        long gameId = JsonHelper.getLong(json, "id");
        long lastActive = JsonHelper.getLong(json, "active");

        long channel = JsonHelper.getLong(json, "channel");
        int mode = JsonHelper.getInt(json, "mode");

        int count = JsonHelper.getInt(json, "count");
        long lastUser = JsonHelper.getLong(json, "last");

        return new MinigameCounting(bot, gameId, channel, mode, count, lastUser, lastActive);
    }

    public void onMessageReceive(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;

        String messageContent = event.getMessage().getContentRaw();

        if (messageContent.isEmpty()) return;

        active();
        long authorId = event.getAuthor().getIdLong();
        String numberString = messageContent.split(" ")[0];
        int status = getStatus(numberString, authorId);

        CommandInfo commandInfo = CommandInfo.create(event, bot);
        if (status == CORRECT_NUMBER) {
            onCorrectNumber(event, commandInfo);
        } else if (status == WRONG_NUMBER) {
            Replyable replyable = Replyable.from(event);
            onWrongNumber(replyable, commandInfo);
        } else if (status == SAME_USER) {
            Replyable replyable = Replyable.from(event);
            onUserRepeat(replyable, commandInfo);
        }
    }

    public int getStatus(String number, long userId) {
        if (userId == lastUser) return SAME_USER;

        String correct = getNext();

        if (correct.equals(number.toLowerCase(Locale.ROOT))) return CORRECT_NUMBER;
        return WRONG_NUMBER;
    }

    public String getCurrent() {

        return switch (modeId) {
            case MODE_BASE_10_ID -> MODE_BASE_10.getValue(count);
            case MODE_HEXADECIMAL_ID -> MODE_HEXADECIMAL.getValue(count);
            case MODE_BINARY_ID -> MODE_BINARY.getValue(count);
            case MODE_LETTERS_ID -> MODE_LETTERS.getValue(count);
            default -> "";
        };
    }

    public String getNext() {
        int next = count + 1;

        return switch (modeId) {
            case MODE_BASE_10_ID -> MODE_BASE_10.getValue(next);
            case MODE_HEXADECIMAL_ID -> MODE_HEXADECIMAL.getValue(next);
            case MODE_BINARY_ID -> MODE_BINARY.getValue(next);
            case MODE_LETTERS_ID -> MODE_LETTERS.getValue(next);
            default -> "";
        };
    }

    public void onCorrectNumber(MessageReceivedEvent event, CommandInfo commandInfo) {
        event.getMessage().addReaction(CORRECT_NUMBER_EMOTE).queue();
        count++;
        lastUser = commandInfo.authorId();
        commandInfo.profile().increaseStat("numbers counted");
    }

    public void onWrongNumber(Replyable replyable, CommandInfo commandInfo) {
        finish(replyable, commandInfo, false);
        replyable.reply("Wrong number! The correct number was " + getNext() + "!");
    }

    public void onUserRepeat(Replyable replyable, CommandInfo commandInfo) {
        finish(replyable, commandInfo, false);
        replyable.reply(commandInfo.authorMention() + " counted twice on row! You reached " + getCurrent() + ".");
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

    public abstract static class Mode {

        public abstract String getValue(int current);

    }

}
