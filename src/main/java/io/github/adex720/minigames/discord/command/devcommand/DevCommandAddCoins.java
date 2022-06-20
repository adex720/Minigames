package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Adds coins for a user.
 *
 * @author adex720
 */
public class DevCommandAddCoins extends DevCommand {

    public DevCommandAddCoins(MinigamesBot bot) {
        super(bot, "add-coins");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args.length <= 1) {
            event.getChannel().sendMessage("Provide user!").queue();
            return true;
        }

        long id = Util.idOrMentionToId(args[1]);
        if (id == -1L) {
            event.getChannel().sendMessage(args[1] + " is not an user!").queue();
            return true;
        }

        if (args.length == 2) {
            event.getChannel().sendMessage("Include amount of coins!").queue();
            return true;
        }

        Profile profile = bot.getProfileManager().getProfile(id);

        if (profile == null) {
            event.getChannel().sendMessage("That user doesn't have a profile!").queue();
            return true;
        }

        int coins;
        try {
            coins = Integer.parseInt(args[2]);
        } catch (Exception ignored) {
            event.getChannel().sendMessage(args[2] + " is not a number!").queue();
            return true;
        }

        profile.addCoins(coins, false, null);
        event.getChannel().sendMessage("Added " + args[2] + " coins to the user!").queue();
        return true;
    }
}
