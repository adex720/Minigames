package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.badge.Badge;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Gives each bot user a badge.
 *
 * @author adex720
 */
public class DevCommandBadgeEveryone extends DevCommand {

    public DevCommandBadgeEveryone(MinigamesBot bot) {
        super(bot, "badge-everyone");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length <= 1) {
            event.getChannel().sendMessage("Provide badge name!").queue();
            return true;
        }

        Badge badge = bot.getBadgeManager().getBadge(args[1]);
        event.getChannel().sendMessage("Added badge " + badge.emojiName() + " for everyone!").queue();

        bot.getBadgeManager().addBadgeForEveryone(badge);
        return true;
    }
}
