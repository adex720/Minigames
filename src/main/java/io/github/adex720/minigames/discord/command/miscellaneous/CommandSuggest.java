package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

/**
 * @author adex720
 */
public class CommandSuggest extends SimpleEmbedReplyCommand {

    public static final String GITHUB_LINK = "https://github.com/adex720/Minigames/issues";

    public CommandSuggest(MinigamesBot bot) {
        super(bot, "suggest", "Sends a link to the bot suggestions on GitHub.",
                "Leave your suggestion [here](" + GITHUB_LINK + ")!",
                "SUGGESTION", "ALWAYS check first if the thing you are suggesting is already suggested!", CommandCategory.MISCELLANEOUS);
        requiresProfile();
    }
}
