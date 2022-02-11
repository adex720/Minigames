package io.github.adex720.minigames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {
        Scanner reader = new Scanner(new File("src/main/resources/words/raw.txt"));

        StringBuilder words = new StringBuilder();
        while (reader.hasNext()) {
            words.append(reader.next().toLowerCase(Locale.ROOT)).append('\n');
        }

        File out = new File("src/main/resources/words/length5.txt");
        if (!out.exists())
            out.createNewFile();

        System.out.println("Starting writing");
        String toWrite = words.toString();
        FileWriter writer = new FileWriter(out);
        writer.write(toWrite);
        writer.flush();

        System.out.println("Finished writing");

    }

}
