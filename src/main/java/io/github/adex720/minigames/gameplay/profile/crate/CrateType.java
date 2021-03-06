package io.github.adex720.minigames.gameplay.profile.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.replyable.Replyable;
import org.jetbrains.annotations.Nullable;

/**
 * A crate can contain coins, boosters all both.
 * If the crate can contain both it randomly picks one each time opened.
 *
 * @author adex720
 * */
public enum CrateType {

    COMMON("common", 0, 150),
    UNCOMMON("uncommon", 1, 200, BoosterRarity.UNCOMMON),
    RARE("rare", 2, 350, BoosterRarity.RARE),
    EPIC("epic", 3, 500, BoosterRarity.EPIC),
    LEGENDARY("legendary", 4, BoosterRarity.LEGENDARY),
    VOTE("vote", 5, BoosterRarity.EPIC),
    GUILD("guild", 6, 1000, BoosterRarity.LEGENDARY),
    COIN("coin", 7, 5000);

    public static final int TYPES_AMOUNT = 7;

    public final String name;
    public final int id;

    public final boolean canContainCoins;
    public final int coins; // 0 if none

    public final boolean canContainBoosters;
    @Nullable
    public final BoosterRarity boosterRarity; // -1 if none

    CrateType(String name, int id, boolean canContainCoins, int coins, boolean canContainBoosters, @Nullable BoosterRarity boosterRarity) {
        this.name = name;
        this.id = id;

        this.canContainCoins = canContainCoins;
        this.coins = coins;

        this.canContainBoosters = canContainBoosters;
        this.boosterRarity = boosterRarity;
    }

    CrateType(String name, int id, int coins) {
        this(name, id, true, coins, false, null);
    }

    CrateType(String name, int id, BoosterRarity boosterRarity) {
        this(name, id, false, 0, true, boosterRarity);
    }

    CrateType(String name, int id, int coins, BoosterRarity boosterRarity) {
        this(name, id, true, coins, true, boosterRarity);
    }

    public String applyRewardsAndGetMessage(Replyable replyable, MinigamesBot bot, Profile owner) {
        boolean isRewardCoins = isRewardCoins(bot);

        if (isRewardCoins) {
            owner.addCoins(this.coins, true, replyable);
            return "You opened a **" + name + "** crate and got **" + this.coins + " coins**!";
        } else {
            owner.addBooster(boosterRarity);
            return "You opened a **" + name + "** crate and got **" + this.boosterRarity + " booster**!";
        }
    }

    public Pair<Integer, Integer> applyRewardsAndReturnCounts(Replyable replyable, MinigamesBot bot, Profile owner) {
        boolean isRewardCoins = isRewardCoins(bot);

        if (isRewardCoins) {
            owner.addCoins(coins, true, replyable);
            return new Pair<>(coins, 0);
        } else {
            owner.addBooster(boosterRarity);
            return new Pair<>(0, 1);
        }
    }

    public boolean isRewardCoins(MinigamesBot bot) {
        if (!canContainCoins) return false;
        if (!canContainBoosters) return true;
        return bot.getRandom().nextBoolean();
    }

    public String getNameWithArticle() {
        return switch (name.charAt(0)) {
            case 'a', 'e', 'i', 'o', 'u' -> "an " + name;
            default -> "a " + name;
        };
    }

    public static CrateType get(int id) {
        for (CrateType type : values()) {
            if (type.id == id) {
                return type;
            }
        }

        throw new IndexOutOfBoundsException("Tried to get crate with id " + id + " but only " + TYPES_AMOUNT + " types exist!");
    }

    public String getEmoteName(MinigamesBot bot) {
        return bot.getEmote("crate_" + name);
    }

}
