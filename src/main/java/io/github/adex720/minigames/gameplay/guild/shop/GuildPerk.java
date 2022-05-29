package io.github.adex720.minigames.gameplay.guild.shop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * @author adex720
 */
public class GuildPerk {

    public final int id;
    public final String name;
    public final String description;
    public final String levelDescription;

    private final int[] prices;
    private final float[] levels;

    public final GuildPerk.Type type;

    /**
     * @param name             Name of the perk. Always shown.
     * @param description      Description of the perk. Only shown on help page.
     * @param levelDescription Text to display after current affection.
     *                         When type is {@link Type#UNLOCK}, only this is displayed.
     * @param prices           Array containing the price of each level.
     * @param levels           Array containing the perks (addition, multiplier) of each level.
     *                         The length of levels array should be one larger than the length of prices,
     *                         because this also contains the perk of level 0.
     */
    public GuildPerk(int id, String name, String description, String levelDescription, int[] prices, float[] levels, Type type) {
        if (prices.length + 1 != levels.length) {
            throw new IllegalStateException("Length of 'levels' must bo one larger than 'prices'.");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.levelDescription = levelDescription;
        this.prices = prices;
        this.levels = levels;
        this.type = type;
    }

    public static GuildPerk fromJson(JsonObject json, int id) {
        String name = JsonHelper.getString(json, "name");
        String description = JsonHelper.getString(json, "description");
        String levelDescription = JsonHelper.getString(json, "effect");

        JsonArray pricesJson = JsonHelper.getJsonArray(json, "prices");
        int[] prices = JsonHelper.jsonArrayToIntArray(pricesJson);

        JsonArray levelsJson = JsonHelper.getJsonArray(json, "effects");
        float[] levels = JsonHelper.jsonArrayToFloatArray(levelsJson);

        int typeId = JsonHelper.getInt(json, "type");
        Type type = Type.get(typeId);

        return new GuildPerk(id, name, description, levelDescription, prices, levels, type);
    }

    public int getMaxLevel() {
        return prices.length;
    }

    /**
     * Returns the price of the next upgrade.
     * If the perk is already maxed, -1 returned.
     */
    public int getPrice(int currentLevel) {
        if (currentLevel > prices.length) return -1;

        return prices[currentLevel];
    }

    /**
     * Returns the price of the next upgrade in format like "Price for next upgrade: X coins".
     * If the perk is already maxed, "This perk is maxed." is returned.
     */
    public String getPriceString(int currentLevel) {
        if (currentLevel >= prices.length) return "This perk is maxed.";

        return "Price for next upgrade: " + prices[currentLevel + 1] + " coins";
    }

    /**
     * Returns the current multiplier or value of the perk.
     */
    public float getCurrentAffection(int currentLevel) {
        if (type == Type.UNLOCK) {
            return (currentLevel > 1) ? 1f : 0f;
        }

        return levels[currentLevel];
    }

    /**
     * Returns 'Unlocked' or 'Not unlocked' depending on if the perk is upgraded.
     */
    public String getLevelAffection(int currentLevel) {
        if (type == Type.ADD) {
            return "+" + levels[currentLevel] + ' ' + levelDescription;
        }

        if (type == Type.MULTIPLY) {
            return "x" + levels[currentLevel] + ' ' + levelDescription;
        }

        if (currentLevel > 1) return "Unlocked";
        return "Not unlocked";
    }

    public MessageEmbed.Field getFieldOnShop(int currentLevel) {
        if (type == Type.UNLOCK) return getFieldOnShopForUnlockable(currentLevel);
        if (currentLevel == prices.length) return getFieldOnShopForMaxedPerk(currentLevel);
        return getFieldOnShopForUnmaxedPerk(currentLevel);
    }

    private MessageEmbed.Field getFieldOnShopForUnlockable(int currentLevel) {
        if (currentLevel == 1) {
            return new MessageEmbed.Field(name, levelDescription + "\nUNLOCKED", true);
        }
        return new MessageEmbed.Field(name, levelDescription + "\nPrice to unlock: " + prices[0], true);
    }

    private MessageEmbed.Field getFieldOnShopForMaxedPerk(int currentLevel) {
        return new MessageEmbed.Field(name, getLevelAffection(currentLevel) +
                "\nLevel: " + currentLevel + "\nThis perk is maxed.", true);
    }

    private MessageEmbed.Field getFieldOnShopForUnmaxedPerk(int currentLevel) {
        return new MessageEmbed.Field(name, getLevelAffection(currentLevel) +
                "\nLevel: " + currentLevel + '/' + getMaxLevel() +
                "\nPrice for next level: " + prices[currentLevel], true);
    }

    public MessageEmbed.Field getFieldOnInformationPage() {
        return new MessageEmbed.Field(name, description, true);
    }

    public enum Type {
        ADD(0),
        MULTIPLY(1),
        UNLOCK(2);

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public static Type get(int id) {
            return switch (id) {
                case 0 -> ADD;
                case 1 -> MULTIPLY;
                case 2 -> UNLOCK;
                default -> throw new IllegalStateException("Unexpected guild perk id: " + id);
            };
        }

        public int getId() {
            return id;
        }
    }
}
