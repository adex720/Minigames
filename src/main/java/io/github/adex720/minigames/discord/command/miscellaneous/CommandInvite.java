package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.SimpleEmbedReplyCommand;

/**
 * @author adex720
 */
public class CommandInvite extends SimpleEmbedReplyCommand {

    public static final String INVITE_LINK = "https://discord.com/api/oauth2/authorize?client_id=814109421118554134&permissions=274878220352&scope=bot%20applications.commands";

    public CommandInvite(MinigamesBot bot) {
        super(bot, "invite", "Invite this bot to your server.",
                "Invite me to another server with [this link](" + INVITE_LINK + ").",
                "INVITE", "Do you want to play minigames on another server?", CommandCategory.MISCELLANEOUS);
    }
}
