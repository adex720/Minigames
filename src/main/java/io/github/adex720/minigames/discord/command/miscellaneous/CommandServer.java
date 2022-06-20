package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

/**
 * @author adex720
 */
public class CommandServer extends SimpleEmbedReplyCommand {

    public static final String SERVER_LINK = "https://discord.gg/u2f6N7mvek";
    public static final long SERVER_ID = 814212042839162900L;

    public CommandServer(MinigamesBot bot) {
        super(bot, "server", "Invites you to the support server.",
                "Join the support server by pressing [this link](" + SERVER_LINK + ").",
                "SUPPORT SERVER", "Do you want to join the support server?", CommandCategory.MISCELLANEOUS);
        requiresProfile();
    }
}
