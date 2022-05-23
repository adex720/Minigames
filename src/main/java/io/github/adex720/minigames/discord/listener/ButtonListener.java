package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author adex720
 */
public class ButtonListener extends ListenerAdapter {

    private final HashMap<String, ButtonManager> BUTTON_MANAGERS;

    private final MinigamesBot bot;

    public ButtonListener(MinigamesBot bot) {
        this.bot = bot;

        BUTTON_MANAGERS = new HashMap<>();
    }

    public void addButtonManager(ButtonManager buttonManager) {
        BUTTON_MANAGERS.put(buttonManager.buttonName, buttonManager);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!event.isFromGuild()) return;
        if (!event.getInteraction().getMember().hasPermission(Permission.MESSAGE_SEND)) return;

        CommandInfo commandInfo = CommandInfo.create(event, bot); // Create command info

        if (bot.getBanManager().isBanned(commandInfo.authorId()))
            return; // Banned users shouldn't be able to use buttons

        String[] args = event.getButton().getId().split("-");

        BUTTON_MANAGERS.get(args[0]).onButtonPressed(event, commandInfo, args);
    }

}
