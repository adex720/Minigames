package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DisconnectListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public DisconnectListener(MinigamesBot bot) {
        this.bot = bot;
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        bot.getLogger().info("Got disconnected, saving data.");
        bot.save();
    }
}
