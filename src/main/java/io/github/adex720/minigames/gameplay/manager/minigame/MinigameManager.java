package io.github.adex720.minigames.gameplay.manager.minigame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manages active minigames.
 * */
public class MinigameManager extends IdCompoundSavableManager<Minigame> {

    private final HashMap<Long, Minigame> MINIGAMES;

    public MinigameManager(MinigamesBot bot) {
        super(bot, "minigame-manager");
        MINIGAMES = new HashMap<>();

        load((JsonArray) bot.loadJson("minigames"));
    }

    @Override
    public Minigame fromJson(JsonObject json) {
        String type = JsonHelper.getString(json, "type");
        return bot.getMinigameTypeManager().getType(type).fromJson(json);
    }

    @Override
    public Set<Minigame> getValues() {
        return new HashSet<>(MINIGAMES.values());
    }

    public void addMinigame(Minigame minigame) {
        MINIGAMES.put(minigame.id, minigame);
    }

    public Minigame getMinigame(long id) {
        return MINIGAMES.get(id);
    }

    public boolean hasMinigame(long id) {
        return MINIGAMES.containsKey(id);
    }

    public void deleteMinigame(long id) {
        MINIGAMES.remove(id);
    }

    @Override
    public void load(JsonArray data) {
        for (JsonElement json : data) {
            addMinigame(fromJson((JsonObject) json));
        }
    }

    public void clearInactiveMinigames() {
        long limit = System.currentTimeMillis() - 1000 * 60 * 30;
        int amount = 0;

        Iterator<Minigame> iterator = MINIGAMES.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isInactive(limit)) {
                iterator.remove();
                amount++;
            }
        }

        bot.getLogger().info("Cleared {} inactive minigame{}.", amount, amount != 1 ? "s" : "");
    }
}
