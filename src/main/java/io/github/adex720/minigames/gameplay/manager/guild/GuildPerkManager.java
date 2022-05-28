package io.github.adex720.minigames.gameplay.manager.guild;

import com.google.gson.JsonElement;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.guild.shop.GuildPerk;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.util.HashMap;

/**
 * @author adex720
 */
public class GuildPerkManager extends Manager {

    private final HashMap<Integer, GuildPerk> PERKS;
    private int perkCount;

    public GuildPerkManager(MinigamesBot bot) {
        super(bot, "guild-perk-manager");
        PERKS = new HashMap<>();
        perkCount = 0;

        init();
    }

    private void init() {
        int count = 0;
        for (JsonElement jsonElement : bot.getResourceJson("guild_perks").getAsJsonArray()) {
            addPerk(GuildPerk.fromJson(jsonElement.getAsJsonObject(), count));
            count++;
        }
    }

    public void addPerk(GuildPerk perk) {
        PERKS.put(perk.id, perk);
        perkCount++;
    }

    public GuildPerk get(int id) {
        return PERKS.get(id);
    }

    public int perkCount() {
        return perkCount;
    }
}
