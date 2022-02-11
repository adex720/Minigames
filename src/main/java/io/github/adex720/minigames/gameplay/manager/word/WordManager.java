package io.github.adex720.minigames.gameplay.manager.word;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class WordManager extends Manager {

    private final ArrayList<String> NICE_WORDS; // length: 4+, 10k most common
    private final int NICE_WORDS_AMOUNT;

    private final ArrayList<String> NICE_WORDLES; // length: 5, 10k most common
    private final int NICE_WORDLES_AMOUNT;

    private final ArrayList<String> LEGTH_OF_5; //length: 5,
    //private final int LENGTH_OF_5_AMOUNT;

    public WordManager(MinigamesBot bot) throws FileNotFoundException {
        super(bot, "word-manager");
        NICE_WORDS = new ArrayList<>();
        NICE_WORDLES = new ArrayList<>();
        LEGTH_OF_5 = new ArrayList<>();

        loadWords();

        NICE_WORDS_AMOUNT = NICE_WORDS.size();
        NICE_WORDLES_AMOUNT = NICE_WORDLES.size();
        //LENGTH_OF_5_AMOUNT = LEGTH_OF_5.size();
    }

    private void loadWords() throws FileNotFoundException {
        loadCommonWords();
        loadLength5();
    }

    private void loadCommonWords() throws FileNotFoundException {
        Scanner reader = new Scanner(bot.getFilePathManager().getWordFile("common.txt"));

        while (reader.hasNext()) {
            String word = reader.nextLine();
            int length = word.length();
            if (length >= 4) {
                NICE_WORDS.add(word);
                if (isValidWordForWordleWord(word)) {
                    NICE_WORDLES.add(word);
                }
            }

        }
    }

    private void loadLength5() throws FileNotFoundException {
        Scanner reader = new Scanner(bot.getFilePathManager().getWordFile("length5.txt"));

        while (reader.hasNext()) {
            LEGTH_OF_5.add(reader.nextLine());
        }
    }

    public String getWordForHangman() {
        return NICE_WORDS.get(ThreadLocalRandom.current().nextInt(NICE_WORDS_AMOUNT));
    }

    public String getWordForWordle() {
        return NICE_WORDLES.get(ThreadLocalRandom.current().nextInt(NICE_WORDLES_AMOUNT));
    }

    public boolean isValidWordForWordle(String word) {
        return LEGTH_OF_5.contains(word);
    }

    public static boolean isValidWordForWordleWord(String word) {
        if (word.length() != 5) return false;

        char[] content = new char[4];

        for (int i = 0; i < 5; i++) {
            char current = word.charAt(i);

            for (int i2 = 0; i2 < i; i2++){
                if (word.charAt(i2) == current) return false; // Word contains same letter twice
            }

            if (i == 4) return true;
            content[i] = current;
        }

        return true; // unreachable

    }


}
