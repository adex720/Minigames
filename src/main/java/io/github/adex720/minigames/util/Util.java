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
        int color = (int) (id + 12582870); // That number is fancy

        int red = color & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = (color >> 16) & 0xFF;

        return new Color(red, green, blue);
    }

    public static boolean isUserNormal(String input) {
        return !input.contains("<@");
    }

}
