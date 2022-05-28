package io.github.adex720.minigames.gameplay.guild.boss;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * @author adex720
 */
public class GuildBoss {

    public final String name;
    public final int id;
    public final int color;

    public final int maxHealth;
    public int currentHealth;

    public final GuildBossReward reward;

    public GuildBoss(String name, int id, int color, int maxHealth, GuildBossReward reward) {
        this.name = name;
        this.id = id;
        this.color = color;

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        this.reward = reward;
    }

    public GuildBoss(String name, int id, int color, int currentHealth, int maxHealth, GuildBossReward reward) {
        this.name = name;
        this.id = id;
        this.color = color;

        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;

        this.reward = reward;
    }

    public static GuildBoss fromJson(JsonObject json) { // TODO: load name, max health and reward from GuildBossList
        String name = JsonHelper.getString(json, "name");
        int id = JsonHelper.getInt(json, "id");
        int color = JsonHelper.getInt(json, "color");

        int currentHealth = JsonHelper.getInt(json, "health");
        int maxHealth = JsonHelper.getInt(json, "max");

        JsonObject rewardsJson = JsonHelper.getJsonObject(json, "reward");
        GuildBossReward reward = GuildBossReward.fromJson(rewardsJson);

        return new GuildBoss(name, id, color, currentHealth, maxHealth, reward);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("id", id);
        json.addProperty("color", color);

        json.addProperty("health", currentHealth);
        json.addProperty("max", maxHealth);

        JsonObject rewardsJson = reward.asJson();
        json.add("reward", rewardsJson);

        return json;
    }

    public void damage() {
        currentHealth--;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * Creates a {@link MessageEmbed.Field} containing information about the boss.
     */
    public MessageEmbed.Field getInfoField() {
        return new MessageEmbed.Field(name + " (" + currentHealth + "/" + maxHealth + ")",
                "Rewards: " + reward.toString(), false);
    }

}
