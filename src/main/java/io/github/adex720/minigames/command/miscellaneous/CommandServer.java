package io.github.adex720.minigames.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.SimpleEmbedReplyCommand;

public class CommandServer extends SimpleEmbedReplyCommand {

    public static final String SERVER_LINK = "https://discord.gg/MUpdS2cMcJ";

    public CommandServer(MinigamesBot bot) {
        super(bot, "server", "Invites you to the support server.",
                "Join the support server by pressing [this link](" + SERVER_LINK + ").",
                "SUPPORT SERVER", "Do you want to join the support server?", CommandCategory.MISCELLANEOUS);
    }
}
