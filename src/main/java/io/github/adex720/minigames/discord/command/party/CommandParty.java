package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.ParentCommand;

/**
 * @author adex720
 */
public class CommandParty extends ParentCommand {

    public CommandParty(MinigamesBot bot) {
        super(bot, "party", "Interacts with parties.", CommandCategory.PARTY);
        requiresProfile();
    }
}
