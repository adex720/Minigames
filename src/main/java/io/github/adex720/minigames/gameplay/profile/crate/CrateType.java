package io.github.adex720.minigames.gameplay.profile.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import org.jetbrains.annotations.Nullable;

public enum CrateType {

    COMMON("common", 0, 150),
    UNCOMMON("uncommon", 1, 200, null),
    RARE("rare", 2, 350, null),
    EPIC("name", 3, 500, null),
    LEGENDARY("name", 4, null),
    VOTE("name", 5, null),
    GUILD("name", 6, 1000, null);

    public static final int TYPES_AMOUNT = 7;

    public final String name;
    public final int id;

    private final boolean canContainCoins;
    @Nullable
    private final int coins;

    private final boolean canContainBoosters;
    @Nullable
    private final BoosterRarity boosterRarity;

    CrateType(String name, int id, boolean canContainCoins, int coins, boolean canContainBoosters, BoosterRarity boosterRarity) {
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

    public String applyRewardsAndGetMessage(MinigamesBot bot, Profile owner) {
        boolean isRewardCoins;

        if (!canContainCoins) isRewardCoins = false;
        else if (!canContainBoosters) isRewardCoins = true;
        else isRewardCoins = bot.getRandom().nextBoolean();

        if (isRewardCoins) {
            owner.addCoins(this.coins, true);
            return "You opened a **" + name + "** crate and got **" + this.coins + " coins**!";
        } else {
            owner.addBooster(boosterRarity);
            return "You opened a **" + name + "** crate and got **" + this.boosterRarity + " booster**!";
        }
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
        return "<:crate_" + name + ":" + bot.getEmoteId(name) + ">";
    }

}
