package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

public class CommandReportBug extends SimpleEmbedReplyCommand {

    public static final String GITHUB_LINK = "https://github.com/adex720/Minigames/issues";

    public CommandReportBug(MinigamesBot bot) {
        super(bot, "report-bug", "Sends a link to the bot suggestions on GitHub.",
                "Report bugs [here](" + GITHUB_LINK + ")!",
                "SUGGEST", "ALWAYS check first if the bug is already reported!", CommandCategory.MISCELLANEOUS);
        requiresProfile();
    }
}
