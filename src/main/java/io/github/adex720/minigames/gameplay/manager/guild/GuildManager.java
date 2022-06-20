package io.github.adex720.minigames.gameplay.manager.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author adex720.
 * @see Guild
 */
public class GuildManager extends IdCompoundSavableManager<Guild> {

    private final HashMap<Long, Guild> GUILDS;

    public GuildManager(MinigamesBot bot) {
        super(bot, "guild-manager");
        GUILDS = new HashMap<>();

        load(bot.loadJson("guilds").getAsJsonArray());
    }

    @Override
    public Set<Guild> getValues() {
        return new HashSet<>(GUILDS.values());
    }

    @Override
    public Guild fromJson(JsonObject json) {
        return Guild.fromJson(json, bot);
    }

    @Override
    public void load(JsonArray data) {
        for (JsonElement json : data) {
            Guild guild = Guild.fromJson(json.getAsJsonObject(), bot);
            GUILDS.put(guild.getId(), guild);
        }
    }

    public Guild create(long owner, String ownerTag, String name) {
        Guild guild = new Guild(bot, owner, ownerTag, name);
        GUILDS.put(owner, guild);
        return guild;
    }

    public Guild remove(long id) {
        Guild guild = GUILDS.remove(id);
        guild.onDelete(bot);
        return guild;
    }

    public void transfer(long previousOwnerId, long newOwnerId, String newOwnerTag) {
        Guild guild = transfer(previousOwnerId, newOwnerId);
        guild.transfer(bot, newOwnerId, newOwnerTag);
    }

    public Guild transfer(long oldId, long newId) {
        Guild guild = GUILDS.remove(oldId);
        GUILDS.put(newId, guild);
        return guild;
    }

    public Guild getById(long ownerId) {
        return GUILDS.get(ownerId);
    }

    public boolean doesGuildExist(String name) {
        for (Guild guild : GUILDS.values()) {
            if (guild.getName().equals(name)) return true;
        }

        return false;
    }

    public Guild getByName(String name) {
        for (Guild guild : GUILDS.values()) {
            if (guild.getName().equals(name)) return guild;
        }

        return null;
    }

    public boolean isGuildOwner(long userId) {
        return GUILDS.containsKey(userId);
    }

    public Guild getGuild(long userId) {
        Guild owned = GUILDS.get(userId);

        if (owned != null) return owned;

        for (Guild guild : GUILDS.values()) {
            if (guild.isGuildMember(userId)) return guild;
        }

        return null;
    }

    public boolean isInGuild(long userId) {
        Guild owned = GUILDS.get(userId);

        if (owned != null) return true;

        for (Guild guild : GUILDS.values()) {
            if (guild.isGuildMember(userId)) return true;
        }

        return false;
    }

    public void onNewWeek() {
        getValues().forEach(guild -> guild.onNewWeek(bot));
        bot.getLogger().info("Reset guild weekly progress!");
    }
}
