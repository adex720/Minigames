package io.github.adex720.minigames.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides many useful methods to interact with {@link JsonObject} and {@link JsonArray}.
 * Examples are:
 * <ul>
 *   <li>- Getting a specific type value from {@link JsonObject}.</li>
 *   <li>- Getting a specific type value from {@link JsonObject} with a default value if no value is present.</li>
 *   <li>- Getting a specific type value from {@link JsonObject} and throwing a custom error if no value is present.</li>
 *   <li>- Converting an Array to {@link JsonArray}.</li>
 *   <li>- Converting a {@link JsonArray} to an Array.</li>
 *   <li>- Converting an {@link ArrayList} to {@link JsonArray}.</li>
 *   <li>- Converting a {@link JsonArray} to an {@link ArrayList}.</li>
 *   <li>- Converting a {@link Set} to {@link JsonArray}.</li>
 *   <li>- Converting a {@link JsonArray} to a {@link Set}.</li>
 * </ul>
 *
 * @author adex720
 */
public class JsonHelper {

    /**
     * Returns the value from the key as int.
     */
    public static int getInt(JsonObject json, String key) {
        return json.get(key).getAsInt();
    }

    /**
     * Returns the value from the key as int.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static int getIntOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsInt();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as int.
     * Returns the default value if the key is not present.
     */
    public static int getInt(JsonObject json, String key, int defaultValue) {
        return json.has(key) ? json.get(key).getAsInt() : defaultValue;
    }

    /**
     * Returns the value from the key as long.
     */
    public static long getLong(JsonObject json, String key) {
        return json.get(key).getAsLong();
    }

    /**
     * Returns the value from the key as Long.
     * If no key is present null is returned.
     */
    public static Long getLongOrNull(JsonObject json, String key) {
        if (!json.has(key)) return null;
        return getLong(json, key);
    }

    /**
     * Returns the value from the key as long.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static long getLongOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsLong();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as long.
     * Returns the default value if the key is not present.
     */
    public static long getLong(JsonObject json, String key, long defaultValue) {
        return json.has(key) ? json.get(key).getAsLong() : defaultValue;
    }

    /**
     * Returns the value from the key as float.
     */
    public static float getFloat(JsonObject json, String key) {
        return json.get(key).getAsFloat();
    }

    /**
     * Returns the value from the key as float.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static float getFloatOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsFloat();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as float.
     * Returns the default value if the key is not present.
     */
    public static float getFloat(JsonObject json, String key, float defaultValue) {
        return json.has(key) ? json.get(key).getAsFloat() : defaultValue;
    }

    /**
     * Returns the value from the key as double.
     */
    public static double getDouble(JsonObject json, String key) {
        return json.get(key).getAsDouble();
    }

    /**
     * Returns the value from the key as double.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static double getDoubleOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsDouble();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as double.
     * Returns the default value if the key is not present.
     */
    public static double getDouble(JsonObject json, String key, double defaultValue) {
        return json.has(key) ? json.get(key).getAsDouble() : defaultValue;
    }

    /**
     * Returns the value from the key as {@link String}.
     */
    public static String getString(JsonObject json, String key) {
        return json.get(key).getAsString();
    }

    /**
     * Returns the value from the key as {@link String}.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static String getStringOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsString();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as {@link String}.
     * Returns the default value if the key is not present.
     */
    public static String getString(JsonObject json, String key, String defaultValue) {
        return json.has(key) ? json.get(key).getAsString() : defaultValue;
    }

    /**
     * Returns the value from the key as boolean.
     */
    public static boolean getBoolean(JsonObject json, String key) {
        return json.get(key).getAsBoolean();
    }

    /**
     * Returns the value from the key as boolean.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static boolean getBooleanOrThrow(JsonObject json, String key, String message) {
        try {
            return json.get(key).getAsBoolean();
        } catch (Exception ignored) {
            throw new JsonSyntaxException(message);
        }
    }

    /**
     * Returns the value from the key as boolean.
     * Returns the default value if the key is not present.
     */
    public static boolean getBoolean(JsonObject json, String key, boolean defaultValue) {
        return json.has(key) ? json.get(key).getAsBoolean() : defaultValue;
    }

    /**
     * Returns the value from the key as {@link JsonObject}.
     */
    public static JsonObject getJsonObject(JsonObject json, String key) {
        return json.get(key).getAsJsonObject();
    }

    /**
     * Returns the value from the key as {@link JsonObject}.
     * If no key is present, a new JsonObject.
     */
    public static JsonObject getJsonObjectOrEmpty(JsonObject json, String key) {
        return getJsonObject(json, key, new JsonObject());
    }

    /**
     * Returns the value from the key as {@link JsonObject}.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static JsonObject getJsonObjectOrThrow(JsonObject json, String key, String message) {
        if (json.has(key)) {
            JsonElement value = json.get(key);
            if (value.isJsonObject())
                return value.getAsJsonObject();
        }

        throw new JsonSyntaxException(message);
    }

    /**
     * Returns the value from the key as {@link JsonObject}.
     * Returns the default value if the key is not present.
     */
    public static JsonObject getJsonObject(JsonObject json, String key, JsonObject defaultValue) {
        return json.has(key) ? json.get(key).getAsJsonObject() : defaultValue;
    }

    /**
     * Returns the value from the key as {@link JsonArray}.
     */
    public static JsonArray getJsonArray(JsonObject json, String key) {
        return json.get(key).getAsJsonArray();
    }

    /**
     * Returns the value from the key as {@link JsonArray}.
     * If no key is present, a new JsonArray.
     */
    public static JsonArray getJsonArrayOrEmpty(JsonObject json, String key) {
        return getJsonArray(json, key, new JsonArray());
    }

    /**
     * Returns the value from the key as {@link JsonArray}.
     * Throws a {@link JsonSyntaxException} if no key is present.
     *
     * @param message Message of the exception.
     */
    public static JsonArray getJsonArrayOrThrow(JsonObject json, String key, String message) {
        if (json.has(key)) {
            JsonElement value = json.get(key);
            if (value.isJsonArray())
                return value.getAsJsonArray();
        }

        throw new JsonSyntaxException(message);
    }

    /**
     * Returns the value from the key as {@link JsonArray}.
     * Returns the default value if the key is not present.
     */
    public static JsonArray getJsonArray(JsonObject json, String key, JsonArray defaultValue) {
        return json.has(key) ? json.get(key).getAsJsonArray() : defaultValue;
    }

    /**
     * Converts a {@link JsonArray} to an array of chars.
     */
    public static char[] jsonArrayToCharArray(JsonArray jsonArray) {
        int size = jsonArray.size();
        char[] array = new char[size];

        for (int i = 0; i < size; i++) {
            array[i] = jsonArray.get(i).getAsCharacter(); // aaaaaaaaaa, Deprecated
        }

        return array;
    }

    /**
     * Converts an array of {@link char}s to a {@link JsonArray}.
     */
    public static JsonArray arrayToJsonArray(char[] array) {
        JsonArray jsonArray = new JsonArray();

        for (char c : array) {
            jsonArray.add(c);
        }

        return jsonArray;
    }

    /**
     * Converts a {@link JsonArray} to an array of int.
     */
    public static int[] jsonArrayToIntArray(JsonArray jsonArray) {
        int size = jsonArray.size();
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = jsonArray.get(i).getAsInt();
        }

        return array;
    }

    /**
     * Converts an array of {@link int}s to a {@link JsonArray}.
     */
    public static JsonArray arrayToJsonArray(int[] array) {
        JsonArray jsonArray = new JsonArray();

        for (int i : array) {
            jsonArray.add(i);
        }

        return jsonArray;
    }

    /**
     * Converts a {@link JsonArray} to an array of long.
     */
    public static long[] jsonArrayToLongArray(JsonArray jsonArray) {
        int size = jsonArray.size();
        long[] array = new long[size];

        for (int i = 0; i < size; i++) {
            array[i] = jsonArray.get(i).getAsLong();
        }

        return array;
    }

    /**
     * Converts an array of {@link long}s to a {@link JsonArray}.
     */
    public static JsonArray arrayToJsonArray(long[] array) {
        JsonArray jsonArray = new JsonArray();

        for (long i : array) {
            jsonArray.add(i);
        }

        return jsonArray;
    }

    /**
     * Converts a {@link JsonArray} to an array of Strings.
     */
    public static String[] jsonArrayToStringArray(JsonArray jsonArray) {
        int size = jsonArray.size();
        String[] array = new String[size];

        for (int i = 0; i < size; i++) {
            array[i] = jsonArray.get(i).getAsString();
        }

        return array;
    }

    /**
     * Converts a {@link JsonArray} to an array of int.
     */
    public static float[] jsonArrayToFloatArray(JsonArray jsonArray) {
        int size = jsonArray.size();
        float[] array = new float[size];

        for (int i = 0; i < size; i++) {
            array[i] = jsonArray.get(i).getAsFloat();
        }

        return array;
    }

    /**
     * Converts an array of {@link int}s to a {@link JsonArray}.
     */
    public static JsonArray arrayToJsonArray(float[] array) {
        JsonArray jsonArray = new JsonArray();

        for (float i : array) {
            jsonArray.add(i);
        }

        return jsonArray;
    }

    /**
     * Converts an array of {@link String}s to a {@link JsonArray}.
     */
    public static JsonArray arrayToJsonArray(String[] array) {
        JsonArray jsonArray = new JsonArray();

        for (String s : array) {
            jsonArray.add(s);
        }

        return jsonArray;
    }

    /**
     * Converts an {@link ArrayList} of ints to a {@link JsonArray}.
     */
    public static JsonArray arrayListIntToJsonArray(ArrayList<Integer> arrayList) {
        JsonArray json = new JsonArray();
        arrayList.forEach(json::add);
        return json;
    }

    /**
     * Converts an {@link JsonArray} to an {@link ArrayList} of ints.
     */
    public static ArrayList<Integer> jsonArrayToIntArrayList(JsonArray json) {
        ArrayList<Integer> arrayList = new ArrayList<>(json.size());
        json.forEach(number -> arrayList.add(number.getAsInt()));
        return arrayList;
    }

    /**
     * Converts an {@link ArrayList} of {@link String}s to a {@link JsonArray}.
     */
    public static JsonArray arrayListStringToJsonArray(ArrayList<String> arrayList) {
        JsonArray json = new JsonArray();
        arrayList.forEach(json::add);
        return json;
    }

    /**
     * Converts an {@link JsonArray} to an {@link ArrayList} of {@link String}s.
     */
    public static ArrayList<String> jsonArrayToStringArrayList(JsonArray json) {
        ArrayList<String> arrayList = new ArrayList<>(json.size());
        json.forEach(number -> arrayList.add(number.getAsString()));
        return arrayList;
    }

    /**
     * Converts an {@link ArrayList} of chars to a {@link JsonArray}.
     */
    public static JsonArray arrayListCharToJsonArray(ArrayList<Character> arrayList) {
        JsonArray json = new JsonArray();
        arrayList.forEach(json::add);
        return json;
    }

    /**
     * Converts an {@link JsonArray} to an {@link ArrayList} of chars.
     */
    public static ArrayList<Character> jsonArrayToCharArrayList(JsonArray json) {
        ArrayList<Character> arrayList = new ArrayList<>(json.size());
        json.forEach(number -> arrayList.add(number.getAsCharacter()));
        return arrayList;
    }

    /**
     * Converts a {@link Set} of ints to a {@link JsonArray}.
     */
    public static JsonArray setIntToJsonArray(Set<Integer> set) {
        JsonArray json = new JsonArray();
        set.forEach(json::add);
        return json;
    }

    /**
     * Converts a {@link JsonArray} to a {@link HashSet} of ints.
     */
    public static HashSet<Integer> jsonArrayToIntHashSet(JsonArray json) {
        HashSet<Integer> arrayList = new HashSet<>(json.size());
        json.forEach(number -> arrayList.add(number.getAsInt()));
        return arrayList;
    }

    /**
     * Converts a {@link Set} of longs to a {@link JsonArray}.
     */
    public static JsonArray setLongToJsonArray(Set<Long> set) {
        JsonArray json = new JsonArray();
        set.forEach(json::add);
        return json;
    }

}
