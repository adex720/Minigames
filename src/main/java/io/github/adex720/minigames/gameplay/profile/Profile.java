package io.github.adex720.minigames.gameplay.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.profile.booster.Booster;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterList;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateList;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.gameplay.profile.quest.Quest;
import io.github.adex720.minigames.gameplay.profile.stat.StatList;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Profile implements IdCompound, JsonSavable<Profile> {

    private final MinigamesBot bot;

    private final long userId;
    private final long created;

    private boolean isInParty;
    private long partyId;

    private int coins;

    private final Set<Integer> badges;

    private final StatList statList;


    private final CrateList crates;

    private final BoosterList boosters;

    private final ArrayList<Booster> activeBoosters;

    public Profile(MinigamesBot bot, long userId) {
        this.bot = bot;
        this.userId = userId;
        created = System.currentTimeMillis();
        isInParty = false;
        partyId = userId;

        coins = 0;
        badges = new HashSet<>();
        statList = new StatList(bot);

        crates = new CrateList();
        boosters = new BoosterList();
        activeBoosters = new ArrayList<>();
    }

    public Profile(MinigamesBot bot, long userId, long crated, int coins, JsonObject statsJson, @Nullable JsonArray questsJson) {
        this.bot = bot;
        this.userId = userId;
        this.created = crated;
        isInParty = false;
        partyId = userId;

        this.coins = coins;
        badges = new HashSet<>();
        statList = new StatList(bot, statsJson);

        if (questsJson != null) {
            bot.getQuestManager().addQuestsFromJson(userId, questsJson);
        }


        crates = new CrateList(); // TODO: load these
        boosters = new BoosterList();
        activeBoosters = new ArrayList<>();
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
        checkBoosterDurations();

        JsonObject json = new JsonObject();

        json.addProperty("id", userId);
        json.addProperty("created", created);

        json.addProperty("coins", coins);
        json.add("stats", statList.asJson());

        json.add("quests", bot.getQuestManager().getQuestJson(userId));

        return json;
    }

    public static Profile fromJson(MinigamesBot bot, JsonObject json) {
        long id = JsonHelper.getLong(json, "id");
        long created = JsonHelper.getLong(json, "created");

        int coins = JsonHelper.getInt(json, "coins");
        JsonObject statsJson = JsonHelper.getJsonObject(json, "stats");

        JsonArray questsJson = JsonHelper.getJsonArray(json, "quests", null);

        return new Profile(bot, id, created, coins, statsJson, questsJson);
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

    public void addCoins(int amount, boolean count) {

        if (count) {
            int finalAmount = (int) (amount * getBoosterMultiplier());

            appendQuests(quest -> quest.coinsEarned(finalAmount, this));
        }
        coins += amount;
    }

    public int getCoins() {
        return coins;
    }

    public void addBadge(int id) {
        badges.add(id);
    }

    public int getStatValue(int id) {
        return statList.getValue(id);
    }

    public int getStatValue(String name) {
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

    public void appendQuests(QuestUpdate... functions) {
        ArrayList<Quest> quests = bot.getQuestManager().getQuests(userId);
        if (quests == null) return;
        for (Quest quest : quests) {
            for (QuestUpdate questUpdate : functions) {
                questUpdate.append(quest);
            }
        }
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

    public boolean hasCrate(int crateType) {
        return crates.amount(crateType) > 0;
    }

    public boolean hasCrate(CrateType crateType) {
        return hasCrate(crateType.id);
    }

    /**
     * @return message to send
     */
    public String openCrate(int type) {
        return openCrate(CrateType.get(type));
    }

    /**
     * @return message to send
     */
    public String openCrate(CrateType type) {
        if (!hasCrate(type)) return "You don't have " + type.getNameWithArticle() + " crate!";

        crates.subtract(type);

        return type.applyRewardsAndGetMessage(bot, this);
    }

    public void addBooster(int rarity) {
        boosters.add(rarity);
    }

    public void addBooster(BoosterRarity rarity) {
        addBooster(rarity.id);
    }

    public boolean hasBooster(int rarity) {
        checkBoosterDurations();
        return boosters.amount(rarity) > 0;
    }

    public boolean hasBooster(BoosterRarity rarity) {
        return hasBooster(rarity.id);
    }

    /**
     * @return message to send
     */
    public String useBooster(int rarity) {
        return useBooster(BoosterRarity.get(rarity));
    }

    /**
     * @return message to send
     */
    public String useBooster(BoosterRarity rarity) {
        if (!hasBooster(rarity)) return "You don't have " + rarity.getNameWithArticle() + "booster!";

        if (rarity.isPersonal && hasActiveBooster(rarity)) {
            return "You already have an active " + rarity.name + " booster!";
        }

        activeBoosters.add(rarity.createBooster());

        return "You used " + rarity.getNameWithArticle() + " booster";
    }

    public boolean hasActiveBooster(int rarity) {
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

    public void checkBoosterDurations() {
        long current = System.currentTimeMillis();
        Iterator<Booster> iterator = activeBoosters.iterator();
        while (iterator.hasNext()) {
            Booster booster = iterator.next();
            if (booster.expiration <= current) iterator.remove();
        }
    }

    public String getBoosters(){
        return boosters.toString(bot);
    }

    public String getCrates(){
        return crates.toString(bot);
    }
}
