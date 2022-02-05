package io.github.adex720.minigames.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class JsonHelper {

    public static int getInt(JsonObject json, String key) {
        return json.get(key).getAsInt();
    }

    public static int getIntOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsInt();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    public static int getInt(JsonObject json, String key, int defaultValue) {
        return json.has(key) ? json.get(key).getAsInt() : defaultValue;
    }

    public static long getLong(JsonObject json, String key) {
        return json.get(key).getAsLong();
    }

    public static long getLongOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsLong();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    public static long getLong(JsonObject json, String key, long defaultValue) {
        return json.has(key) ? json.get(key).getAsLong() : defaultValue;
    }

    public static float getFloat(JsonObject json, String key) {
        return json.get(key).getAsFloat();
    }

    public static float getFloatOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsFloat();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    public static float getFloat(JsonObject json, String key, float defaultValue) {
        return json.has(key) ? json.get(key).getAsFloat() : defaultValue;
    }

    public static double getDouble(JsonObject json, String key) {
        return json.get(key).getAsDouble();
    }

    public static double getDoubleOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsDouble();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    public static double getDouble(JsonObject json, String key, double defaultValue) {
        return json.has(key) ? json.get(key).getAsDouble() : defaultValue;
    }

    public static String getString(JsonObject json, String key) {
        return json.get(key).getAsString();
    }

    public static String getStringOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsString();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    public static String getString(JsonObject json, String key, String defaultValue) {
        return json.has(key) ? json.get(key).getAsString() : defaultValue;
    }

    public static boolean getBoolean(JsonObject json, String key) {
        return json.get(key).getAsBoolean();
    }

    public static boolean getBoolean(JsonObject json, String key, boolean defaultValue) {
        return json.has(key) ? json.get(key).getAsBoolean() : defaultValue;
    }

    public static JsonObject getJsonObject(JsonObject json, String key) {
        return json.get(key).getAsJsonObject();
    }

    public static JsonObject getJsonObjectOrThrow(JsonObject json, String key, String message) {
        if (json.has(key)) {
            JsonElement value = json.get(key);
            if (value.isJsonObject())
                return value.getAsJsonObject();
        }

        throw new JsonSyntaxException(message);
    }

    public static JsonObject getJsonObject(JsonObject json, String key, JsonObject defaultValue) {
        return json.has(key) ? json.get(key).getAsJsonObject() : defaultValue;
    }

    public static JsonArray getJsonArray(JsonObject json, String key) {
        return json.get(key).getAsJsonArray();
    }

    public static JsonArray getJsonArrayOrThrow(JsonObject json, String key, String message) {
        if (json.has(key)) {
            JsonElement value = json.get(key);
            if (value.isJsonArray())
                return value.getAsJsonArray();
        }

        throw new JsonSyntaxException(message);
    }

    public static JsonArray getJsonArray(JsonObject json, String key, JsonArray defaultValue) {
        return json.has(key) ? json.get(key).getAsJsonArray() : defaultValue;
    }

}
