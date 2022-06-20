package io.github.adex720.minigames;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.network.HttpsRequester;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class runs bunch of task which are faster for computer than human.
 * (For example turning white background transparent)
 * The code can be bad and hard to read, since the only purpose of the class is to make testing small task easier.
 *
 * @author adex720
 */
public class Test {

    public static void main(String[] args) {
      testQuestionOptionArranging();
    }

    public static void testQuestionOptionArranging() {

        for (int i = 0; i < 0x18; i++) {
            System.out.println(arrangeQuestionOptions(i));
        }
    }

    public static String arrangeQuestionOptions(int optionsShuffleSeed) {
        int correctAnswerIndex = optionsShuffleSeed & 0x3;

        int firstIncorrectAnswerIndex = (optionsShuffleSeed >> 3) & 0x3;
        int secondIncorrectAnswerIndex = (optionsShuffleSeed >> 2) & 0x1;
        int thirdIncorrectAnswerIndex = 0;

        String seedString = optionsShuffleSeed >= 10 ? optionsShuffleSeed + " " : " " + optionsShuffleSeed + " ";
        String valuesStart = seedString + "|correct|1st|2nd|3rd\nRAW|   " + correctAnswerIndex + "   | "
                + firstIncorrectAnswerIndex + " | " + secondIncorrectAnswerIndex + " | " + thirdIncorrectAnswerIndex;

        if (secondIncorrectAnswerIndex >= firstIncorrectAnswerIndex) secondIncorrectAnswerIndex++;

        if (thirdIncorrectAnswerIndex >= firstIncorrectAnswerIndex) thirdIncorrectAnswerIndex++;
        if (thirdIncorrectAnswerIndex >= secondIncorrectAnswerIndex) {
            thirdIncorrectAnswerIndex++;
            if (thirdIncorrectAnswerIndex == firstIncorrectAnswerIndex) thirdIncorrectAnswerIndex++;
        }

        if (firstIncorrectAnswerIndex >= correctAnswerIndex) firstIncorrectAnswerIndex++;
        if (secondIncorrectAnswerIndex >= correctAnswerIndex) secondIncorrectAnswerIndex++;
        if (thirdIncorrectAnswerIndex >= correctAnswerIndex) thirdIncorrectAnswerIndex++;

        String valuesEnd = "\nEND|   " + correctAnswerIndex + "   | "
                + firstIncorrectAnswerIndex + " | " + secondIncorrectAnswerIndex + " | " + thirdIncorrectAnswerIndex + "\n";

        boolean valid = true;
        if (correctAnswerIndex == firstIncorrectAnswerIndex) valid = false;
        else if (correctAnswerIndex == secondIncorrectAnswerIndex) valid = false;
        else if (correctAnswerIndex == thirdIncorrectAnswerIndex) valid = false;

        else if (firstIncorrectAnswerIndex == secondIncorrectAnswerIndex) valid = false;
        else if (firstIncorrectAnswerIndex == thirdIncorrectAnswerIndex) valid = false;

        else if (secondIncorrectAnswerIndex == thirdIncorrectAnswerIndex) valid = false;

        return valuesStart + valuesEnd + "VALID: " + valid + "\n";
    }

    public static void testTriviaWebRequests() {
        String address = "https://opentdb.com/api.php?amount=10&type=multiple&encode=url3986";
        HttpsRequester httpsRequester = new HttpsRequester();

        try {
            System.out.println(httpsRequester.requestString(address));
        } catch (Exception e) {
            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }

    }

    public static void testCharacterBytes() {
        String string = "語";
        char firstChar = string.charAt(0);
        System.out.println("String: " + string);
        System.out.println("First char: " + firstChar);
        System.out.println("As int: " + (int) firstChar);
    }

    public static void testWebRequest() throws IOException {
        HttpsRequester requester = new HttpsRequester();

        JsonArray response = requester.requestJson("https://api.github.com/repos/adex720/Minigames/stargazers").getAsJsonArray();

        for (JsonElement jsonElement : response) {
            JsonObject userJson = (JsonObject) jsonElement;

            String name = JsonHelper.getString(userJson, "login");

        }

    }

    public static void generateMastermindHintpinEmotes() throws IOException {
        String pathDefault = "src/main/resources/textures/emotes/minigames/mastermind_";

        Color colorCorrect = new Color(0xD62C17);
        Color colorWrongPlace = new Color(0xFCFCFC);


        int size = 256;
        int half = size / 2;
        int offset = 8;
        int markDiameter = half - offset - offset;

        for (int correct = 0; correct <= 4; correct++) {
            for (int wrong = correct; wrong <= 4; wrong++) {
                String path = pathDefault + correct + (wrong - correct) + ".png";

                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = image.getGraphics();

                Color transparent = new Color(255, 255, 255, 0);
                graphics.setColor(transparent);
                graphics.fillRect(0, 0, size, size);

                graphics.setColor(colorCorrect);
                for (int i = 0; i < 4; i++) {
                    if (i >= wrong) break;
                    if (i >= correct) {
                        graphics.setColor(colorWrongPlace);
                    }

                    int x = (i & 1) == 0 ? offset : half + offset;
                    int y = i < 2 ? offset : half + offset;

                    graphics.fillOval(x, y, markDiameter, markDiameter);
                }

                graphics.dispose();
                File file = new File(path);
                file.createNewFile();
                ImageIO.write(image, "png", file);
            }
        }

    }
}