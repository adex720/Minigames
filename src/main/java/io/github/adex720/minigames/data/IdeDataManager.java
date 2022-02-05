package io.github.adex720.minigames.data;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IdeDataManager extends DataManager {

    private final String path;

    public IdeDataManager(String path) {
        super();
        this.path = path;
    }

    @Override
    public JsonElement loadJson(String name) {
        String filePath = path + name + ".json";

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No file exists in " + filePath);

            return new JsonArray();
        }


        JsonArray json;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            json = gson.fromJson(reader, JsonArray.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new JsonArray();
        }

        return json;
    }

    @Override
    public boolean saveJson(JsonElement json, String name) {
        String filePath = path + name + ".json";

        try {
            gson.toJson(json, new FileWriter(filePath));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

}
