package io.github.adex720.minigames.gameplay.manager.word;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class WordManager extends Manager {

    private final ArrayList<String> HANGMAN_WORDS; // length: 4+, 10k most common
    private final int HANGMAN_WORDS_AMOUNT;

    private final ArrayList<String> UNSCRAMBLE_WORDS; // length: 6+, 10k most common
    private final int UNSCRAMBLE_WORDS_AMOUNT;

    private final ArrayList<String> COMMON_WORDLES; // length: 5, 10k most common
    private final int COMMON_WORDLES_AMOUNT;

    private final ArrayList<String> LEGTH_OF_5; //length: 5,
    //private final int LENGTH_OF_5_AMOUNT;

    public WordManager(MinigamesBot bot) throws FileNotFoundException {
        super(bot, "word-manager");
        HANGMAN_WORDS = new ArrayList<>();
        UNSCRAMBLE_WORDS = new ArrayList<>();
        COMMON_WORDLES = new ArrayList<>();
        LEGTH_OF_5 = new ArrayList<>();

        loadWords();

        HANGMAN_WORDS_AMOUNT = HANGMAN_WORDS.size();
        UNSCRAMBLE_WORDS_AMOUNT = UNSCRAMBLE_WORDS.size();
        COMMON_WORDLES_AMOUNT = COMMON_WORDLES.size();
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
                HANGMAN_WORDS.add(word);
                if (isValidWordForWordleWord(word)) {
                    COMMON_WORDLES.add(word);
                } else if(length >= 6){
                    UNSCRAMBLE_WORDS.add(word);
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
        return HANGMAN_WORDS.get(ThreadLocalRandom.current().nextInt(HANGMAN_WORDS_AMOUNT));
    }

    public String getWordForUnscramble() {
        return UNSCRAMBLE_WORDS.get(ThreadLocalRandom.current().nextInt(UNSCRAMBLE_WORDS_AMOUNT));
    }

    public String getWordForWordle() {
        return COMMON_WORDLES.get(ThreadLocalRandom.current().nextInt(COMMON_WORDLES_AMOUNT));
    }

    public boolean isValidWordForWordle(String word) {
        return LEGTH_OF_5.contains(word);
    }

    public static boolean isValidWordForWordleWord(String word) {
        if (word.length() != 5) return false;

        char[] content = new char[4];

        for (int i = 0; i < 5; i++) {
            char current = word.charAt(i);

            for (int i2 = 0; i2 < i; i2++) {
                if (word.charAt(i2) == current) return false; // Word contains same letter twice
            }

            if (i == 4) return true;
            content[i] = current;
        }

        return true; // unreachable

    }


}
