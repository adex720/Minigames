package io.github.adex720.minigames.data;

import com.google.gson.JsonObject;

public interface JsonSavable<S> {

    JsonObject getAsJson();

}
