package io.github.adex720.minigames.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * This object has a list of objects can be converted into a {@link JsonArray} containing each element as {@link JsonObject}
 * The objects can be loaded from JsonObject.
 *
 * @author adex720
 * */
public interface JsonConvertible<T extends JsonSavable<T>> {

    T fromJson(JsonObject json);

    default JsonObject asJson(T object) {
        return object.getAsJson();
    }


    default Set<T> setFromJson(JsonArray jsonArray) {
        Set<T> elements = new HashSet<>();

        for (JsonElement json : jsonArray) {
            if (!json.isJsonObject()) throw new IllegalArgumentException("JsonArray contains non jsonObject entry");
            elements.add(fromJson(json.getAsJsonObject()));
        }

        return elements;
    }

    default JsonArray asJson() {
        JsonArray jsonArray = new JsonArray();

        for (T element : getValues()) {
            jsonArray.add(asJson(element));
        }

        return jsonArray;
    }

    Set<T> getValues();
}
