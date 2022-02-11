package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public ButtonListener(MinigamesBot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        CommandInfo commandInfo = CommandInfo.create(event, bot);

        bot.getReplayManager().onButtonPress(event, commandInfo);
    }
}