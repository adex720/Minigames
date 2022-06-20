package io.github.adex720.minigames.gameplay.guild.boss;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author adex720
 */
public record GuildBossReward(int coins,
                              @Nullable CrateType crate,
                              int crateCount,
                              @Nullable BoosterRarity booster,
                              int boosterCount) {

    public GuildBossReward(int coins, @Nullable CrateType crate, int crateCount, @Nullable BoosterRarity booster, int boosterCount) {
        this.coins = coins;
        this.crate = crate;
        this.crateCount = crateCount;
        this.booster = booster;
        this.boosterCount = boosterCount;
    }

    public GuildBossReward(int coins, @Nonnull CrateType crate, @Nonnull BoosterRarity booster) {
        this(coins, crate, 1, booster, 1);
    }

    public GuildBossReward(int coins, @Nonnull CrateType crate, int crateCount) {
        this(coins, crate, crateCount, null, 0);
    }

    public GuildBossReward(int coins, @Nonnull BoosterRarity crate, int boosterCount) {
        this(coins, null, 0, crate, boosterCount);
    }

    public GuildBossReward(int coins) {
        this(coins, null, 0, null, 0);
    }

    public GuildBossReward(@Nonnull CrateType crate, int crateCount, @Nonnull BoosterRarity booster, int boosterCount) {
        this(0, crate, crateCount, booster, boosterCount);
    }

    public GuildBossReward(@Nonnull CrateType crate, @Nonnull BoosterRarity booster) {
        this(0, crate, 1, booster, 1);
    }

    public GuildBossReward(@Nonnull CrateType crate, int crateCount) {
        this(0, crate, crateCount, null, 0);
    }

    public GuildBossReward(@Nonnull BoosterRarity crate, int boosterCount) {
        this(0, null, 0, crate, boosterCount);
    }

    public static GuildBossReward fromJson(JsonObject json) {
        int coins = JsonHelper.getInt(json, "coins", 0);

        boolean hasCrates = json.has("crate");
        boolean hasBoosters = json.has("booster");

        if (hasCrates && hasBoosters) {
            JsonObject cratesJson = JsonHelper.getJsonObject(json, "crate");
            int crateTypeId = JsonHelper.getInt(cratesJson, "type");
            int crateCount = JsonHelper.getInt(cratesJson, "count", 1);

            JsonObject boostersJson = JsonHelper.getJsonObject(json, "booster");
            int boosterRarityId = JsonHelper.getInt(boostersJson, "type");
            int boosterCount = JsonHelper.getInt(boostersJson, "count", 1);

            return new GuildBossReward(coins, CrateType.get(crateTypeId), crateCount, BoosterRarity.get(boosterRarityId), boosterCount);
        }

        if (hasCrates) {
            JsonObject cratesJson = JsonHelper.getJsonObject(json, "crate");
            int typeId = JsonHelper.getInt(cratesJson, "type");
            int count = JsonHelper.getInt(cratesJson, "count", 1);

            return new GuildBossReward(coins, CrateType.get(typeId), count);
        }

        if (hasBoosters) {
            JsonObject boostersJson = JsonHelper.getJsonObject(json, "booster");
            int rarityId = JsonHelper.getInt(boostersJson, "type");
            int count = JsonHelper.getInt(boostersJson, "count", 1);

            return new GuildBossReward(coins, BoosterRarity.get(rarityId), count);
        }

        return new GuildBossReward(coins);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        if (coins > 0) json.addProperty("coins", coins);

        if (crateCount > 0) {
            JsonObject cratesJson = new JsonObject();

            cratesJson.addProperty("type", crate.id);
            if (crateCount > 1) cratesJson.addProperty("count", crateCount);

            json.add("crate", cratesJson);
        }

        if (boosterCount > 0) {
            JsonObject boostersJson = new JsonObject();

            boostersJson.addProperty("type", booster.id);
            if (boosterCount > 1) boostersJson.addProperty("count", boosterCount);

            json.add("booster", boostersJson);
        }

        return json;
    }

    public void apply(MinigamesBot bot, Guild guild) {

        long guildOwnerId = guild.getId();
        for (long userId : guild.getMemberIds()) {
            Profile profile = bot.getProfileManager().getProfile(userId);
            addCoinsAndCrates(profile);

            if (userId == guildOwnerId) applyBoosters(profile);
        }

    }

    private void addCoinsAndCrates(Profile profile) {
        if (crateCount > 0) profile.addCrates(crate, crateCount);
        if (coins > 0) profile.addCoins(coins, true, Replyable.IGNORE_ALL);
    }

    private void applyBoosters(Profile profile) {
        if (boosterCount > 0) profile.addBoosters(booster, boosterCount);
    }

    @Override
    public String toString() {
        boolean appended = false;
        StringBuilder stringBuilder = new StringBuilder();

        if (coins > 0) {
            stringBuilder.append(coins).append(" coins");
            appended = true;
        }

        if (crateCount > 0) {
            if (appended) stringBuilder.append('\n');
            appended = true;

            stringBuilder.append(crateCount).append(", ").append(crate.name).append(" crates");
            if (crateCount > 1) stringBuilder.append('s');
        }

        if (boosterCount > 0) {
            if (appended) stringBuilder.append(", ");

            stringBuilder.append(boosterCount).append(' ').append(booster.name).append(" booster");
            if (boosterCount > 1) stringBuilder.append('s');

            stringBuilder.append(" (Only for the owner)");
        }

        return stringBuilder.toString();
    }
}
