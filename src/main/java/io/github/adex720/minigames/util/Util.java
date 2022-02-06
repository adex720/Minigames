package io.github.adex720.minigames.util;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Util {

    public static void schedule(Task task, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay);
    }

    @FunctionalInterface
    public interface Task {
        void run();
    }

    public static Color getColor(long id) {
        int color = (int) ((id + 5595956) % 0xffffff);

        int red = color % 256;
        int green = (color / 0xff) % 256;
        int blue = (color / 0xffff) % 256;

        return new Color(red, green, blue);
    }

}
