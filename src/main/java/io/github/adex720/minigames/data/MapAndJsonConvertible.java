package io.github.adex720.minigames.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Set;

/**
 * This object has a {@link HashMap} containing objects than can be saved as {@link com.google.gson.JsonObject}
 *
 * @author adex720
 */
public interface MapAndJsonConvertible<T extends JsonSavable<T> & IdCompound> extends JsonConvertible<T> {


    default HashMap<Long, T> mapFromJson(JsonArray jsonArray) {
        HashMap<Long, T> elements = new HashMap<>();

        for (JsonElement json : jsonArray) {
            if (!json.isJsonObject()) throw new IllegalArgumentException("JsonArray contains non jsonObject entry");
            T element = fromJson(json.getAsJsonObject());

            elements.put(element.getId(), element);
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
