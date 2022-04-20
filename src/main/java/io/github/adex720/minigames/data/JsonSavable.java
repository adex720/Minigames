package io.github.adex720.minigames.data;

import com.google.gson.JsonObject;

/**
 * This object can be saved as a {@link JsonObject}
 *
 * @author adex720
 * */
public interface JsonSavable<S> {

    JsonObject getAsJson();

}
