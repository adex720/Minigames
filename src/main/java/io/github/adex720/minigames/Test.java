package io.github.adex720.minigames;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {

        String[] paths = {a("common"), a("uncommon"), a("rare"), a("epic"), a("legendary"), a("guild"), a("vote")};

        for (String path : paths) {
            File file = new File(path);
            BufferedImage image = ImageIO.read(file);

            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = newImage.getGraphics();

            Color transparent = new Color(255, 255, 255, 0);
            graphics.setColor(transparent);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = image.getRGB(x, y);

                    if ((rgb & 0xff) >= 250 && ((rgb >> 8) & 0xff) >= 250 && ((rgb >> 16) & 0xff) >= 250
                            || 2 * y - 317 > x
                            || -2 * y + 525 < x
                            || -0.07f * y + 213 < x) {
                        graphics.setColor(transparent);
                    } else {
                        graphics.setColor(new Color(rgb));
                    }
                    graphics.drawLine(x, y, x, y);
                }

            }

            graphics.dispose();
            ImageIO.write(newImage, "png", file);
        }

        /*Scanner reader = new Scanner(new File("src/main/resources/words/raw.txt"));

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

        System.out.println("Finished writing");*/

    }

    private static String a(String name) {
        return "src/main/resources/textures/emotes/crates/crate_" + name + ".png";
    }

}
