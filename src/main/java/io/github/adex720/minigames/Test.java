package io.github.adex720.minigames;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class runs bunch of task which are faster for computer than human.
 * (For example turning white background transparent)
 *
 * @author adex720
 */
public class Test {

    public static void main(String[] args) throws IOException {
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