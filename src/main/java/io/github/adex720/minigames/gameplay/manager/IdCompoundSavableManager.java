package io.github.adex720.minigames.gameplay.manager;

import com.google.gson.JsonArray;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.data.MapAndJsonConvertible;

public abstract class IdCompoundSavableManager<T extends IdCompound & JsonSavable<T>> extends Manager implements MapAndJsonConvertible<T> {

    public IdCompoundSavableManager(MinigamesBot bot, String name) {
        super(bot, name);
    }

    public abstract void loadProfiles(JsonArray data);
}
