package io.github.adex720.minigames.gameplay.manager.file;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.io.File;

public class FilePathManager extends Manager {

    public FilePathManager(MinigamesBot bot) {
        super(bot, "file-path-manager");
    }

    public File getFile(String name){
        return new File("src/main/resources/" + name);
    }

    public File getWordFile(String name){
        return getFile("words/" + name);
    }
}
