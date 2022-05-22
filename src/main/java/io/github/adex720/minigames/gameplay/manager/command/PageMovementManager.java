package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.PageCommand;
import io.github.adex720.minigames.gameplay.manager.Manager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;

/**
 * Edits messages which have multiple pages.
 *
 * @author adex720
 * @see PageCommand
 */
public class PageMovementManager extends Manager {

    private final HashMap<String, PageCommand> COMMANDS;

    public PageMovementManager(MinigamesBot bot) {
        super(bot, "page-manager");

        COMMANDS = new HashMap<>();
    }

    public void registerPageCommand(PageCommand command) {
        COMMANDS.put(command.name, command);
    }

    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo commandInfo, String[] args) {
        long userId = Long.parseLong(args[3]);
        if (userId != commandInfo.authorId()) return;

        String type = args[1];
        int page = Integer.parseInt(args[2]);

        PageCommand command = COMMANDS.get(type);
        if (command == null) {
            bot.getLogger().error("Failed to move page on {}, can't find command", type);
            return;
        }

        command.onPageMove(event, commandInfo, args, page);
    }
}
