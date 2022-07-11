package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author adex720
 */
public class DisconnectListener extends ListenerAdapter {

    private final MinigamesBot bot;
    private boolean shouldSave;
    private boolean shouldReconnect;

    public DisconnectListener(MinigamesBot bot) {
        this.bot = bot;
        shouldSave = true;
        shouldReconnect = true;
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        if (!shouldSave) return;
        bot.getLogger().info("Got disconnected, saving data.");
        bot.save();
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        if (!shouldReconnect) return;
        bot.getLogger().error("Reconnection failed, FORCING RESTART!");
        bot.forceRestart();
    }

    public void saveOnReconnect() {
        this.shouldSave = true;
    }

    public void doNotSaveOnReconnect() {
        this.shouldSave = false;
    }

    public boolean doesSaveOnReconnect() {
        return shouldSave;
    }

    public void autoReconnect() {
        this.shouldReconnect = true;
    }

    public void doNotAutoReconnect() {
        this.shouldReconnect = false;
    }

    public boolean doesAutoReconnect() {
        return shouldReconnect;
    }
}
