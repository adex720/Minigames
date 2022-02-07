package io.github.adex720.minigames.gameplay.manager;

import io.github.adex720.minigames.MinigamesBot;
import org.jetbrains.annotations.Nullable;

public abstract class Manager {

    public final String name;

    protected final MinigamesBot bot;

    protected Manager(MinigamesBot bot, String name) {
        this.name = name;
        this.bot = bot;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
