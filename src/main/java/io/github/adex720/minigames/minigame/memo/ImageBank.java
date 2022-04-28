package io.github.adex720.minigames.minigame.memo;

import com.google.gson.JsonArray;
import io.github.adex720.minigames.MinigamesBot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author adex720
 */
public class ImageBank {

    public static final int SIZE = 50;

    public final BufferedImage background;

    private final Polygon[][] polygons;
    private final Color[] colors;

    public ImageBank(MinigamesBot bot) {
        BufferedImage background1;
        File file = bot.getFilePathManager().getFile("textures/minigames/memo_background.png");
        try {
            background1 = ImageIO.read(file);
        } catch (IOException e) {
            background1 = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_4BYTE_ABGR);
            e.printStackTrace();
        }
        background = background1;

        polygons = new Polygon[8][];
        colors = new Color[]{
                new Color(0xb80b19),
                new Color(0xa31d7d),
                new Color(0x3e068c),
                new Color(0x204499),
                new Color(0x0a918d),
                new Color(0x099c13),
                new Color(0xdeae1f),
                new Color(0x663f17)
        };

        initPolygons(bot);
    }

    private void initPolygons(MinigamesBot bot) {
        JsonArray cardsJson = bot.getResourceJson("memo_polygons").getAsJsonArray();

        int cardsCount = cardsJson.size();
        for (int cardId = 0; cardId < cardsCount; cardId++) {
            JsonArray polygonsJson = cardsJson.get(cardId).getAsJsonArray(); // polygons of one card

            int polygonsCount = polygonsJson.size();
            Polygon[] cardPolygons = new Polygon[polygonsCount];
            for (int polygonId = 0; polygonId < polygonsCount; polygonId++) {
                JsonArray pointsJson = polygonsJson.get(polygonId).getAsJsonArray(); // points of one polygon

                int pointsCount = pointsJson.size();
                int[] xs = new int[pointsCount];
                int[] ys = new int[pointsCount];

                for (int pointId = 0; pointId < pointsCount; pointId++) {
                    JsonArray pointJson = pointsJson.get(pointId).getAsJsonArray(); // coordinates of one point

                    xs[pointId] = pointJson.get(0).getAsInt();
                    ys[pointId] = pointJson.get(1).getAsInt();
                }

                Polygon polygon = new Polygon(xs, ys, pointsCount);
                cardPolygons[polygonId] = polygon;
            }

            this.polygons[cardId] = cardPolygons;
        }
    }

    public void drawCard(Graphics g, int x, int y, int id) {
        int polygonId = (id & 0x38) >> 3;
        int colorId = id & 0x7;

        g.setColor(Color.WHITE); // make white background
        g.fillRect(x, y, SIZE, SIZE);

        g.setColor(colors[colorId]);
        for (Polygon polygonBase : polygons[polygonId]) {
            Polygon polygonMoved = new Polygon(polygonBase.xpoints, polygonBase.ypoints, polygonBase.npoints);
            polygonMoved.translate(x, y);
            g.fillPolygon(polygonMoved);
        }
    }

    public void drawBackground(Graphics g, int x, int y) {
        g.drawImage(background, x, y, null);
    }
}
