package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Removes a ban from a user.
 */
public class DevCommandUnban extends DevCommand {

    public DevCommandUnban(MinigamesBot bot) {
        super(bot, "unban");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length <= 1) {
            event.getChannel().sendMessage("Provide user id!").queue();
            return true;
        }

        long id;
        try {
            id = Long.parseLong(args[1]);
        } catch (Exception e) {
            event.getChannel().sendMessage(args[1] + " is not an user id!").queue();
            return true;
        }

        if (bot.getBanManager().unban(id)) {
            event.getChannel().sendMessage("That user is not banned").queue();
            return true;
        }
        event.getChannel().sendMessage("Unbanned <@!" + id + ">!").queue();
        return true;
    }
}
