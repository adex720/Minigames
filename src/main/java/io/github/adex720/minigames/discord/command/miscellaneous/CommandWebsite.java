package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

/**
 * @author adex720
 */
public class CommandWebsite extends SimpleEmbedReplyCommand {

    public static final String WEBSITE = "http://adexminigames.eu/";

    public CommandWebsite(MinigamesBot bot) {
        super(bot, "website", "Sends a link to the official website.",
                "Visit the official website with [this link](" + WEBSITE + ").",
                "WEBSITE", "Do you want to explore the bot on web?", CommandCategory.MISCELLANEOUS);
        requiresProfile();
    }
}
