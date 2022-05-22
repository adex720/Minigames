package io.github.adex720.minigames.discord.command;

import java.util.Locale;

/**
 * @author adex720
 */
public enum CommandCategory {

    MINIGAME(0),
    PARTY(1),
    USER(2),
    MISCELLANEOUS(3);

    public final int id;
    public final String name;

    CommandCategory(int id) {
        this.id = id;
        this.name = name().toLowerCase(Locale.ROOT);
    }

    /**
     * Returns null on invalid id.
     */
    public static CommandCategory get(int id) {
        for (CommandCategory category : values()) {
            if (category.id == id) return category;
        }

        return null;
    }

    /**
     * Returns null on invalid name.
     */
    public static CommandCategory get(String name) {
        for (CommandCategory category : values()) {
            if (category.name.equals(name)) return category;
        }

        return null;
    }
}
