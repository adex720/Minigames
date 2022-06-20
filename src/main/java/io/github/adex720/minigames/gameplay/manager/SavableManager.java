package io.github.adex720.minigames.gameplay.manager;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.JsonConvertible;
import io.github.adex720.minigames.data.JsonSavable;

/**
 * This manager manages a list of objects that can be saved.
 *
 * @author adex720
 * */
public abstract class SavableManager<T extends JsonSavable> extends Manager implements JsonConvertible<T> {

    public SavableManager(MinigamesBot bot, String name) {
        super(bot, name);
    }

    @Override
    public JsonObject asJson(T object) {
        return JsonConvertible.super.asJson(object);
    }
}
