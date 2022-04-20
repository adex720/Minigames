package io.github.adex720.minigames.gameplay.profile.booster;

import io.github.adex720.minigames.MinigamesBot;

/**
 * @author adex720
 */
public enum BoosterRarity {

    COMMON("common", 0, 5, 1.3f, true),
    UNCOMMON("uncommon", 1, 10, 1.5f, true),
    RARE("rare", 2, 10, 2f, true),
    EPIC("epic", 3, 15, 2.25f, true),
    LEGENDARY("legendary", 4, 20, 2.5f, true),
    GUILD_SHORT("common guild", 5, 15, 2f, false),
    GUILD_MEDIUM("rare guild", 6, 30, 2f, false),
    GUILD_LONG("legendary guild", 6, 30, 2.5f, false),
    GLOBAL("global", 7, 30, 2f, false);

    public static final int RARITIES_AMOUNT = 8;

    public final String name;
    public final int id;

    public final int durationMinutes;
    public final float multiplier;

    public final boolean isPersonal;

    BoosterRarity(String name, int id, int durationMinutes, float multiplier, boolean isPersonal) {
        this.name = name;
        this.id = id;
        this.durationMinutes = durationMinutes;
        this.multiplier = multiplier;
        this.isPersonal = isPersonal;
    }

    public String getNameWithArticle() {
        return switch (name.charAt(0)) {
            case 'a', 'e', 'i', 'o', 'u' -> "an " + name;
            default -> "a " + name;
        };
    }

    public String getKeyName() {
        return name.replace(" ", "_");
    }

    public Booster createBooster() {
        return new Booster(this, System.currentTimeMillis() + durationMinutes * 60000L);
    }

    public static BoosterRarity get(int id) {
        for (BoosterRarity rarity : values()) {
            if (rarity.id == id) {
                return rarity;
            }
        }

        throw new IndexOutOfBoundsException("Tried to get booster with id " + id + " but only " + RARITIES_AMOUNT + " rarities exist!");
    }

    public String getEmoteName(MinigamesBot bot) {
        return bot.getEmote("booster_" + getKeyName());
    }
}
