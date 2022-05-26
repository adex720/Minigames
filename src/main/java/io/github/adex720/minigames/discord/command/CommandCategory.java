package io.github.adex720.minigames.discord.command;

import java.util.Locale;

/**
 * @author adex720
 */
public enum CommandCategory {

    MINIGAME(0),
    USER(1),
    PARTY(2),
    GUILD(3),
    MISCELLANEOUS(4);

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
