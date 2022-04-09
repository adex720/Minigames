package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DevCommandBan extends DevCommand {

    public DevCommandBan(MinigamesBot bot) {
        super(bot, "ban");
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

        bot.getBanManager().ban(id);
        event.getChannel().sendMessage("Banned <@!" + id + ">!").queue();
        return true;
    }
}
