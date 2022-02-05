package io.github.adex720.minigames.manager;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.JsonConvertible;
import io.github.adex720.minigames.data.JsonSavable;

public abstract class SavableManager<T extends JsonSavable<T>> extends Manager implements JsonConvertible<T> {

    public SavableManager(MinigamesBot bot, String name) {
        super(bot, name);
    }

    @Override
    public JsonObject asJson(T object) {
        return JsonConvertible.super.asJson(object);
    }
}
