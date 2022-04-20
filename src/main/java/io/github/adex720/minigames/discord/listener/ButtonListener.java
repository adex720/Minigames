package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author adex720
 */
public class ButtonListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public ButtonListener(MinigamesBot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (!event.isFromGuild()) return;
        if (!event.getInteraction().getMember().hasPermission(Permission.MESSAGE_SEND)) return;

        CommandInfo commandInfo = CommandInfo.create(event, bot); // Create command info

        if (bot.getBanManager().isBanned(commandInfo.authorId()))
            return; // Banned users shouldn't be able to use buttons


        bot.getReplayManager().onButtonPress(event, commandInfo); // For replay button after finishing a minigame
    }
}
