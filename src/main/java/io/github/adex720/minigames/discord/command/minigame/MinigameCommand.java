package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.ParentCommand;

/**
 * @author adex720
 */
public class MinigameCommand extends ParentCommand {

    public MinigameCommand(MinigamesBot bot, String name, String description) {
        super(bot, name, description, CommandCategory.MINIGAME);
        requiresProfile();
    }

}
