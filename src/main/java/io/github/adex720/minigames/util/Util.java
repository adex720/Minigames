package io.github.adex720.minigames.util;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contains a bunch of utility methods and fields which don't have a clear class associated with them.
 *
 * @author adex720
 */
public class Util {

    public static final int MINIGAMES_COLOR = 0x3D6bf4;
    public static final long MINIGAMES_BOT_ID = 814109421118554134L;


    public static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

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

    public static int getColor(long id) {
        int color = (int) (id + 12582870); // That number is fancy
        return color & 0xFFFFFF;
    }

    public static boolean isUserNormal(String input) {
        return !input.contains("<@");
    }

    public static String formatTime(int seconds) {
        int secondsDifference = seconds % 60;
        int minutes = seconds / 60;

        if (minutes < 60) {
            return minutes + "m" + secondsDifference + "s";
        }

        int minutesDifference = minutes % 60;
        int hours = minutes / 60;

        return hours + "h" + minutesDifference + "m" + secondsDifference + "s";
    }

    public static String formatTime(Duration duration) {
        return formatTime((int) duration.toSeconds());
    }

    public static String formatNumber(long number) {
        String numberUnformatted = Long.toString(number);
        StringBuilder numberFormatted = new StringBuilder();

        int length = numberUnformatted.length();

        int mod = length % 3;

        boolean first = true;
        int id = 0;
        var iter = numberUnformatted.chars().iterator();
        while (iter.hasNext()) {
            char letter = (char) (int) (iter.next());
            if (first) {
                first = false;
                numberFormatted.append(letter);
                continue;
            }

            id++;

            if (id % 3 == mod) {
                numberFormatted.append(',');
            }
            numberFormatted.append(letter);
        }

        return numberFormatted.toString();
    }

    public static int getMillisecondsUntilUtcMidnight() {
        return MILLISECONDS_IN_DAY - (int) (System.currentTimeMillis() % MILLISECONDS_IN_DAY);
    }


    public static String capitalizeFirstLetter(String string) {
        if (string.isEmpty()) return "";
        char first = string.charAt(0);

        if (first < 'a' || first > 'z') return string;
        String firstCapital = Character.toString((char) (first - 0x20));

        if (string.length() == 1) return firstCapital; // Lower case letter - capital letter = 32 aka 0x20.

        return firstCapital + string.substring(1);
    }

    /**
     * Compares the values with == -operator.
     */
    public static<T> boolean hasPureDuplicateValues(T[] values){
        for (int i = 0; i < values.length; i++){
            T value = values[i];

            for (int i2 = i+1; i2 < values.length; i2++){
                if (values[i2] == value) return true;
            }
        }

        return false;
    }

    /**
     * Compares the values with {@link Object#equals(Object)}.
     */
    public static<T> boolean hasEqualDuplicateValues(T[] values){
        for (int i = 0; i < values.length; i++){
            T value = values[i];

            for (int i2 = i+1; i2 < values.length; i2++){
                if (values[i2] == value) return true;
            }
        }

        return false;
    }

    /**
     * Compares the values with == -operator.
     */
    public static boolean hasPureDuplicateValues(int[] values){
        for (int i = 0; i < values.length; i++){
            int value = values[i];

            for (int i2 = i+1; i2 < values.length; i2++){
                if (values[i2] == value) return true;
            }
        }

        return false;
    }

}
