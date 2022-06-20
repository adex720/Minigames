package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.ParentCommand;

/**
 * @author adex720
 */
public class CommandGuild extends ParentCommand {

    public CommandGuild(MinigamesBot bot) {
        super(bot, "guild", "Interacts with guilds.", CommandCategory.GUILD);
        requiresProfile();
    }
}
