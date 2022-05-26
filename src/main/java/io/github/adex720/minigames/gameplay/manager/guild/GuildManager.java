package io.github.adex720.minigames.gameplay.manager.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;

import java.util.HashMap;
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
    }

    @Override
    public Set<Guild> getValues() {
        return (Set<Guild>) GUILDS.values();
    }

    @Override
    public Guild fromJson(JsonObject json) {
        return Guild.fromJson(json);
    }

    @Override
    public void load(JsonArray data) {
        for (JsonElement json : data){
            Guild guild = Guild.fromJson(json.getAsJsonObject());
            GUILDS.put(guild.getId(), guild);
        }
    }

    public Guild create(long owner, String name) {
        Guild guild = new Guild(owner, name);
        GUILDS.put(owner, guild);
        return guild;
    }

    public Guild remove(long id) {
        return GUILDS.remove(id);
    }

    public Guild getById(long ownerId) {
        return GUILDS.get(ownerId);
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

    public boolean isInGuide(long userId) {
        Guild owned = GUILDS.get(userId);

        if (owned != null) return true;

        for (Guild guild : GUILDS.values()) {
            if (guild.isGuildMember(userId)) return true;
        }

        return false;
    }

    public void onNewWeek(){
        getValues().forEach(Guild::onNewWeek);
        bot.getLogger().info("Reset guild weekly progress!");
    }
}
