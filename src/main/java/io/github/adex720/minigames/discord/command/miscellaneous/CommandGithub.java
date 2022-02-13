package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

public class CommandGithub extends SimpleEmbedReplyCommand {

    public static final String GITHUB_LINK = "https://github.com/adex720/Minigames";

    public CommandGithub(MinigamesBot bot) {
        super(bot, "github", "Sends a link to the bot source.",
                "Find the source code in [here](" + GITHUB_LINK + ").",
                "GITHUB", "Do you want to view the bot source code?", CommandCategory.MISCELLANEOUS);
    }
}
