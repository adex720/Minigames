package io.github.adex720.minigames.gameplay.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.discord.command.user.crate.KitCommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameManager;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.badge.Badge;
import io.github.adex720.minigames.gameplay.profile.booster.Booster;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterList;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateList;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.gameplay.profile.quest.Quest;
import io.github.adex720.minigames.gameplay.profile.settings.PlayerSettings;
import io.github.adex720.minigames.gameplay.profile.settings.Setting;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;
import io.github.adex720.minigames.gameplay.profile.stat.StatList;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Profile stores most of the data a player has.
 * The class also contains many useful methods interacting with profiles.
 *
 * @author adex720
 */
public class Profile implements IdCompound, JsonSavable<Profile> {

    private final MinigamesBot bot;

    private final long userId;
    private String tag;
    private final long created;

    private boolean banned;

    private boolean isInParty;
    private long partyId;

    private int coins;

    private final Set<Integer> badges;
    private final StatList statList;


    private final CrateList crates;
    private final BoosterList boosters;

    private final ArrayList<Booster> activeBoosters;


    private final PlayerSettings playerSettings;

    public Profile(MinigamesBot bot, long userId, String tag) {
        created = System.currentTimeMillis();

        this.bot = bot;
        this.userId = userId;
        this.tag = tag;

        this.banned = false;

        isInParty = false;
        partyId = userId;

        coins = 0;
        badges = new HashSet<>();
        statList = new StatList(bot);

        crates = new CrateList();
        boosters = new BoosterList();
        activeBoosters = new ArrayList<>();

        playerSettings = new PlayerSettings(userId);
    }

    public Profile(MinigamesBot bot, long userId, String tag, long crated, int coins,
                   JsonObject statsJson, @Nullable JsonArray questsJson, JsonObject cratesJson, JsonObject boostersJson,
                   JsonArray activeBoostersJson, JsonArray statusesJson, JsonArray settingsJson, JsonArray badgesJson) {
        this.bot = bot;
        this.userId = userId;
        this.tag = tag;
        this.created = crated;

        banned = false;

        isInParty = false;
        partyId = userId;

        this.coins = coins;
        badges = JsonHelper.jsonArrayToIntHashSet(badgesJson);
        statList = new StatList(bot, statsJson);

        if (questsJson != null) {
            bot.getQuestManager().addQuestsFromJson(userId, questsJson);
        }

        crates = CrateList.fromJson(cratesJson);
        boosters = BoosterList.fromJson(boostersJson);
        activeBoosters = new ArrayList<>(activeBoostersJson.size());
        activeBoostersJson.forEach(b -> activeBoosters.add(Booster.fromJson(b)));
        checkBoosterDurations();

        playerSettings = PlayerSettings.fromJson(bot, settingsJson, userId);

        for (JsonElement status : statusesJson) {
            switch (status.getAsString()) {
                case "banned" -> {
                    banned = true;
                    bot.getBanManager().ban(userId);
                }
            }
        }
    }

    public static Profile create(MinigamesBot bot, long id, String tag) {
        return new Profile(bot, id, tag);
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public JsonObject getAsJson() {
        checkBoosterDurations();

        JsonObject json = new JsonObject();

        json.addProperty("id", userId);
        json.addProperty("tag", tag);
        json.addProperty("created", created);

        json.addProperty("coins", coins);
        json.add("stats", statList.asJson());

        if (bot.getQuestManager().hasLoadedQuests(userId))
            json.add("quests", bot.getQuestManager().getQuestJson(userId));

        checkBoosterDurations();
        if (!crates.isEmpty()) json.add("crates", crates.asJson());
        if (!boosters.isEmpty()) json.add("boosters", boosters.asJson());
        if (!activeBoosters.isEmpty()) json.add("active-boosters", getActiveBoostersJson());

        JsonArray statusesJson = getStatusesJson();
        if (!statusesJson.isEmpty()) json.add("statuses", statusesJson);

        JsonArray settingsJson = playerSettings.getAsJsonArray();
        if (!settingsJson.isEmpty()) json.add("settings", settingsJson);

        if (!badges.isEmpty()) json.add("badges", JsonHelper.setIntToJsonArray(badges));

        return json;
    }

    public static Profile fromJson(MinigamesBot bot, JsonObject json) {
        long id = JsonHelper.getLong(json, "id");
        String tag = JsonHelper.getString(json, "tag", "");

        long created = JsonHelper.getLong(json, "created");

        int coins = JsonHelper.getInt(json, "coins");
        JsonObject statsJson = JsonHelper.getJsonObject(json, "stats");

        JsonArray questsJson = JsonHelper.getJsonArray(json, "quests", null);

        JsonObject cratesJson = JsonHelper.getJsonObject(json, "crates", new JsonObject());
        JsonObject boostersJson = JsonHelper.getJsonObject(json, "boosters", new JsonObject());
        JsonArray activeBoostersJson = JsonHelper.getJsonArray(json, "active-boosters", new JsonArray());

        JsonArray statusesJson = JsonHelper.getJsonArray(json, "statuses", new JsonArray());

        JsonArray settingsJson = JsonHelper.getJsonArray(json, "settings", new JsonArray());

        JsonArray badgesJson = JsonHelper.getJsonArray(json, "badges", new JsonArray());

        return new Profile(bot, id, tag, created, coins, statsJson, questsJson, cratesJson, boostersJson, activeBoostersJson, statusesJson, settingsJson, badgesJson);
    }

    private JsonArray getStatusesJson() {
        JsonArray json = new JsonArray();

        if (banned) json.add("banned");

        return json;
    }

    private JsonArray getActiveBoostersJson() {
        JsonArray json = new JsonArray();

        for (Booster booster : activeBoosters) {
            json.add(booster.getAsJson());
        }

        return json;
    }

    public boolean isInParty() {
        return isInParty;
    }

    /**
     * Returns the id of the party the user is in.
     * If the user is not in a party this value is either the id of the previous party or the id of the user.
     * If it is not certain if the user is in a party {@link Profile#isInParty()} should be checked.
     */
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

    public String getTag() {
        if (tag.isEmpty()) requestTag();
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Makes a request to Discord receiving the user tag.
     * <p>
     * If the request doesn't return a tag the tag is not chanced.
     * If it failed, and the current value at {@link Profile#tag} is empty, it is set to "unknown user".
     */
    private void requestTag() {
        bot.getJda().retrieveUserById(userId).queue(v -> {
            String result = v.getAsTag();

            if (!result.isEmpty()) {
                tag = result;
                return;
            }

            if (tag.isEmpty()) tag = "unknown user";
        });
    }

    /**
     * @param amount    amount of coins.
     * @param count     should the amount be affected by current multiplier and counted towards quests and stats.
     * @param replyable must be non-null if {@param count} is true.
     *                  Use {@link Replyable#IGNORE_ALL} if needed.
     */
    public void addCoins(int amount, boolean count, Replyable replyable) {

        if (count) {
            int finalAmount = (int) (amount * getBoosterMultiplier());

            statList.increaseStat("coins earned", finalAmount, this);
            coins += finalAmount;

            appendQuests(quest -> quest.coinsEarned(replyable, finalAmount, this));
        } else {
            coins += amount;
        }
    }

    public int getCoins() {
        return coins;
    }

    public void removeCoins(int amount) {
        coins -= amount;
    }

    /**
     * Adds the badge with the given id.
     * Badges are only visual and give no benefit.
     */
    public void addBadge(int id) {
        badges.add(id);
    }

    @CheckReturnValue
    public int getStatValue(int id) {
        return statList.getValue(id);
    }

    @CheckReturnValue
    public int getStatValue(String name) {
        return statList.getValue(name);
    }

    @CheckReturnValue
    public int getStatValue(Stat stat) {
        return statList.getValue(stat.id());
    }

    public void setStatValue(int id, int value) {
        statList.setValue(id, value, this);
    }

    public void setStatValue(String name, int value) {
        statList.setValue(name, value, this);
    }

    public int increaseStat(String stat) {
        return statList.increaseStat(stat, this);
    }

    public int increaseStat(int stat) {
        return statList.increaseStat(stat, this);
    }

    public int increaseStat(Stat stat) {
        return statList.increaseStat(stat, this);
    }

    public int increaseStat(String stat, int amount) {
        return statList.increaseStat(stat, amount, this);
    }

    public int increaseStat(int stat, int amount) {
        return statList.increaseStat(stat, amount, this);
    }

    public int increaseStat(Stat stat, int amount) {
        return statList.increaseStat(stat, amount, this);
    }

    /**
     * @return embed message containing information about profile
     */
    public MessageEmbed getProfileEmbed(User user, MinigamesBot bot) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("PROFILE")
                .setColor(Util.getColor(userId));

        StringBuilder text = new StringBuilder();

        text.append(getBadgesString()); // Add badges

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

    /**
     * @return String containing all badges as emotes. A new line is included if any badges exists.
     */
    public String getBadgesString() {
        ArrayList<Badge> badges = bot.getBadgeManager().getBadges(this.badges);
        if (!badges.isEmpty()) {
            StringBuilder badgesText = new StringBuilder();

            badges.forEach((badge -> badgesText.append(' ').append(badge.getEmoji())));

            return badgesText.append('\n').toString();
        }

        return "";
    }

    /**
     * Applies the given function to each quest
     */
    public void appendQuests(QuestUpdate... functions) {
        ArrayList<Quest> quests = bot.getQuestManager().getQuests(userId);
        if (quests == null) return;
        for (Quest quest : quests) {
            for (QuestUpdate questUpdate : functions) {
                questUpdate.append(quest);
            }
        }
    }

    public int amountOfUnfinishedQuests() {
        int amount = 0;
        for (Quest quest : bot.getQuestManager().getQuests(userId)) {
            if (!quest.isCompleted()) amount++;
        }
        return amount;
    }

    /**
     * Applies the rewards, increases stats and sends a message.
     */
    public void dailyQuestsCompleted(Replyable replyable) {
        addCrate(CrateType.LEGENDARY);

        Stat current = bot.getStatManager().get("current daily quest streak");
        Stat highest = bot.getStatManager().get("highest daily quest streak");
        int streak = increaseStat(current);
        if (getStatValue(current) > getStatValue(highest)) {
            increaseStat(highest);
        }

        replyable.reply("You finished all of your quests today! You received a legendary crate." +
                "\nYour current streak of completing all quests is " + streak + ".");
    }


    @FunctionalInterface
    public interface QuestUpdate {
        void append(Quest quest);
    }

    public void addCrate(int type) {
        crates.add(type);
    }

    public void addCrate(CrateType type) {
        addCrate(type.id);
    }

    public void addCrates(int type, int count) {
        while (count > 0) {
            crates.add(type);
            count--;
        }
    }

    public void addCrates(CrateType type, int count) {
        addCrates(type.id, count);
    }

    public boolean hasCrate(int crateType) {
        return crates.amount(crateType) > 0;
    }

    public boolean hasCrate(CrateType crateType) {
        return hasCrate(crateType.id);
    }

    /**
     * @return message to send
     */
    public String openCrate(Replyable replyable, int type) {
        return openCrate(replyable, CrateType.get(type));
    }

    /**
     * @return message to send
     */
    public String openCrate(Replyable replyable, CrateType type) {
        if (!hasCrate(type)) {
            replyable.reply("You don't have any " + type.name() + " crates!");
            return "";
        }

        crates.subtract(type);

        return type.applyRewardsAndGetMessage(replyable, bot, this);
    }

    /**
     * Returns -1 if no crates
     */
    public int getFirstCrateRarityOnInventory() {
        for (int i = 0; i < CrateType.TYPES_AMOUNT; i++) {
            if (crates.amount(i) > 0) return i;
        }

        return -1;
    }

    public void addBooster(int rarity) {
        boosters.add(rarity);
    }

    public void addBoosters(int rarity, int amount) {
        while (amount > 0) {
            boosters.add(rarity);
            amount--;
        }
    }

    public void addBooster(BoosterRarity rarity) {
        addBooster(rarity.id);
    }

    public void addBoosters(BoosterRarity rarity, int amount) {
        addBoosters(rarity.id, amount);
    }

    public boolean hasBooster(int rarity) {
        checkBoosterDurations();
        return boosters.amount(rarity) > 0;
    }

    public boolean hasBooster(BoosterRarity rarity) {
        return hasBooster(rarity.id);
    }

    /**
     * @return message to send.
     */
    public String useBooster(Replyable replyable, int rarity) {
        appendQuests(q -> q.boosterUsed(replyable, this));
        return useBooster(BoosterRarity.get(rarity));
    }

    /**
     * @return message to send.
     */
    public String useBooster(BoosterRarity rarity) {
        if (!hasBooster(rarity))
            return "You don't have " + rarity.getEmoteName(bot) + " " + rarity.name + " booster!";

        if (rarity.isPersonal && hasActiveBooster(rarity)) {
            return "You already have an active " + rarity.getEmoteName(bot) + " " + rarity.name + " booster!";
        }

        activeBoosters.add(rarity.createBooster());

        return "You used " + rarity.getEmoteName(bot) + " " + rarity.name + " booster";
    }

    public boolean hasActiveBooster(int rarity) {
        checkBoosterDurations();
        for (Booster booster : activeBoosters) {
            if (booster.rarity.id == rarity) return true;
        }

        return false;
    }

    public boolean hasActiveBooster(BoosterRarity rarity) {
        return hasActiveBooster(rarity.id);
    }

    public float getBoosterMultiplier() {
        checkBoosterDurations();
        float multiplier = 1f;

        for (Booster booster : activeBoosters) {
            multiplier *= booster.rarity.multiplier;
        }

        return multiplier;
    }

    /**
     * Removes expired boosters.
     */
    public void checkBoosterDurations() {
        long current = System.currentTimeMillis();
        activeBoosters.removeIf(booster -> booster.expiration <= current);
    }

    public String getBoosters() {
        return boosters.toString(bot);
    }

    public String getCrates() {
        return crates.toString(bot);
    }

    public BoosterList getBoostersList() {
        return boosters;
    }

    public CrateList getCrateList() {
        return crates;
    }

    public int amountOfCrates() {
        return crates.size();
    }

    public int amountOfCrates(int id) {
        return crates.amount(id);
    }


    public String getKitCooldown(int kitId, OffsetDateTime current) {
        KitCommand command = bot.getKitCooldownManager().getKitCommand(kitId);
        return command.getCooldownFull(userId, current);
    }

    public String getKitCooldowns() {
        OffsetDateTime current = OffsetDateTime.now();
        StringBuilder cooldowns = new StringBuilder();

        boolean newLine = false;
        for (int i = 0; i < KitCommand.KITS_COUNT; i++) {
            if (newLine) cooldowns.append('\n');
            newLine = true;

            cooldowns.append(getKitCooldown(i, current));
        }

        return cooldowns.toString();
    }

    /**
     * @return embed message containing stats of the player.
     */
    public MessageEmbed getStatsMessage(User user) {
        return new EmbedBuilder()
                .setTitle(user.getAsTag())
                .addField("Stats", getStats(), false)
                .setColor(Util.getColor(userId))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    /**
     * @return list of each stat the player has a value greater than 0 in.
     */
    public String getStats() {
        StringBuilder statsString = new StringBuilder();
        boolean newLine = false;
        for (Stat stat : bot.getStatManager().getLeaderboardStats()) {
            int value = statList.getValue(stat.id());
            if (value == 0) continue;

            if (newLine) statsString.append('\n');
            newLine = true;

            statsString.append(stat.name()).append(" - ").append(Util.formatNumber(value));
        }

        return statsString.toString();
    }

    public void ban() {
        banned = true;
    }

    public void unban() {
        banned = false;
    }

    public boolean isBanned() {
        return banned;
    }

    public boolean getSetting(Setting setting) {
        return playerSettings.getValue(setting);
    }

    public boolean getSetting(String setting) {
        return getSetting(bot.getSettingsList().get(setting));
    }

    public boolean getSetting(int setting) {
        return getSetting(bot.getSettingsList().get(setting));
    }

    public void setSetting(Setting setting, boolean value) {
        playerSettings.set(setting, value);
    }

    /**
     * @return {@link Setting}
     */
    public Setting setSetting(String settingName, boolean value) {
        Setting setting = bot.getSettingsList().get(settingName);
        setSetting(setting, value);
        return setting;
    }

    /**
     * @return {@link Setting}
     */
    public Setting setSetting(int settingId, boolean value) {
        Setting setting = bot.getSettingsList().get(settingId);
        setSetting(setting, value);
        return setting;
    }

    public void onDelete(SlashCommandInteractionEvent event) {
        bot.getQuestManager().removeQuests(userId);
        bot.getKitCooldownManager().clearCooldowns(userId);

        MinigameManager minigameManager = bot.getMinigameManager();
        if (isInParty) {
            Party party = bot.getPartyManager().getParty(partyId);

            if (party.getOwnerId() == userId) {
                // Delete party and its minigame
                Minigame minigame = minigameManager.getMinigame(partyId);
                if (minigame != null) {
                    // This also removes it from the list.
                    // The result of the minigame is sent so other people from the party will see it.
                    event.getHook().sendMessage(minigame.quit(Replyable.from(event))).queue();
                }

                party.clearInvites();
                party.onDelete();
                bot.getPartyManager().removeParty(partyId);
            } else {
                party.onMemberLeave(userId); // Remove user from party
                party.removeMember(userId);
            }

        } else {
            bot.getMinigameManager().deleteMinigame(userId); // If you delete your profile you don't need to see the results
        }
    }

}
