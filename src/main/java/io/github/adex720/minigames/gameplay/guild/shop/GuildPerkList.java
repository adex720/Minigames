package io.github.adex720.minigames.gameplay.guild.shop;

import com.google.gson.JsonArray;
import io.github.adex720.minigames.gameplay.manager.guild.GuildPerkManager;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * @author adex720
 */
public class GuildPerkList {

    private final GuildPerkManager guildPerkManager;
    private final int[] levels;

    public GuildPerkList(GuildPerkManager guildPerkManager) {
        this.guildPerkManager = guildPerkManager;
        this.levels = new int[guildPerkManager.perkCount()];
    }

    public GuildPerkList(GuildPerkManager guildPerkManager, int[] levels) {
        this.guildPerkManager = guildPerkManager;
        this.levels = levels;
    }

    public static GuildPerkList fromJson(JsonArray json, GuildPerkManager guildPerkManager) {
        return new GuildPerkList(guildPerkManager, JsonHelper.jsonArrayToIntArray(json));
    }

    public JsonArray asJson() {
        return JsonHelper.arrayToJsonArray(levels);
    }

    public boolean isMaxed(int perkId) {
        return levels[perkId] >= guildPerkManager.get(perkId).getMaxLevel();
    }

    public void upgrade(int perkId) {
        levels[perkId]++;
    }

    public int getPrice(int perkId) {
        return guildPerkManager.get(perkId).getPrice(levels[perkId]);
    }

    public int getLevel(int perkId) {
        return levels[perkId];
    }

    /**
     * Returns the name of the perk.
     */
    public String getName(int perkId) {
        return guildPerkManager.get(perkId).name;
    }

    /**
     * Returns a list of {@link MessageEmbed.Field}s to display on the shop.
     */
    public MessageEmbed.Field[] getFieldsOnShop() {
        int count = levels.length;

        MessageEmbed.Field[] fields = new MessageEmbed.Field[count];
        for (int i = 0; i < count; i++) {
            fields[i] = guildPerkManager.get(i).getFieldOnShop(levels[i]);
        }

        return fields;
    }

    /**
     * Returns a list of {@link MessageEmbed.Field}s to display on the perk information.
     */
    public MessageEmbed.Field[] getFieldsOnPerkInfo() {
        int count = levels.length;

        MessageEmbed.Field[] fields = new MessageEmbed.Field[count];
        for (int i = 0; i < count; i++) {
            fields[i] = guildPerkManager.get(i).getFieldOnInformationPage();
        }

        return fields;
    }
}
