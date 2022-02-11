package io.github.adex720.minigames.gameplay.manager.minigame;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.IdCompoundSavableManager;
import io.github.adex720.minigames.minigame.Minigame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MinigameManager extends IdCompoundSavableManager<Minigame> {

    private final HashMap<Long, Minigame> MINIGAMES;

    public MinigameManager(MinigamesBot bot) {
        super(bot, "minigame-manager");
        MINIGAMES = new HashMap<>();
    }

    @Override
    public Minigame fromJson(JsonObject json) {
        return null;
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
}
