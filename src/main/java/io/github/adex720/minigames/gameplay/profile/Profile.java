package io.github.adex720.minigames.gameplay.profile;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.profile.stat.StatList;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Profile implements IdCompound, JsonSavable<Profile> {

    private final MinigamesBot bot;

    private final long userId;
    private final long created;

    private boolean isInParty;
    private long partyId;

    private int coins;

    private final Set<Integer> badges;

    private final StatList statList;

    public Profile(MinigamesBot bot, long userId) {
        this.bot = bot;
        this.userId = userId;
        created = System.currentTimeMillis();
        isInParty = false;
        partyId = userId;

        coins = 0;
        badges = new HashSet<>();
        statList = new StatList(bot);
    }

    public Profile(MinigamesBot bot, long userId, long crated, int coins, JsonObject statsJson) {
        this.bot = bot;
        this.userId = userId;
        this.created = crated;
        isInParty = false;
        partyId = userId;

        this.coins = coins;
        badges = new HashSet<>();
        statList = new StatList(bot, statsJson);
    }

    public static Profile create(MinigamesBot bot, long id) {
        return new Profile(bot, id);
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("id", userId);
        json.addProperty("created", created);

        json.addProperty("coins", coins);

        return json;
    }

    public static Profile fromJson(MinigamesBot bot, JsonObject json) {
        long id = JsonHelper.getLong(json, "id");
        long created = JsonHelper.getLong(json, "created");

        int coins = JsonHelper.getInt(json, "coins");
        JsonObject statsJson = JsonHelper.getJsonObject(json, "stats");

        return new Profile(bot, id, created, coins, statsJson);
    }

    public boolean isInParty() {
        return isInParty;
    }

    public long getPartyId() {
        return partyId;
    }

    public void partyJoined(long partyId) {
        isInParty = true;
        this.partyId = partyId;
    }

    public void partyLeft() {
        isInParty = false;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public int getCoins() {
        return coins;
    }

    public void addBadge(int id) {
        badges.add(id);
    }

    public int getValue(int id) {
        return statList.getValue(id);
    }

    public int getValue(String name) {
        return statList.getValue(name);
    }

    public void increaseStat(String stat) {
        statList.increaseStat(stat);
    }

    public void increaseStat(String stat, int amount) {
        statList.increaseStat(stat, amount);
    }

    public void increaseStat(int stat) {
        statList.increaseStat(stat);
    }

    public void increaseStat(int stat, int amount) {
        statList.increaseStat(stat, amount);
    }

    public MessageEmbed getEmbed(User user, MinigamesBot bot) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("PROFILE")
                .setColor(Util.getColor(userId));

        StringBuilder text = new StringBuilder();

        ArrayList<Badge> badges = bot.getBadgeManager().getBadges(this.badges);
        if (!badges.isEmpty()) {
            StringBuilder badgesText = new StringBuilder();

            badges.forEach((badge -> badgesText.append(' ').append(badge)));

            text.append(badgesText);
        }


        text.append("Coins: ").append(coins);
        if (isInParty) {
            text.append("\nIn party of <@!").append(partyId).append('>');
        } else {
            text.append("\nNot in a party");
        }


        embedBuilder.addField(user.getAsTag(), text.toString(), true);

        return embedBuilder.setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant()).build();
    }
}
