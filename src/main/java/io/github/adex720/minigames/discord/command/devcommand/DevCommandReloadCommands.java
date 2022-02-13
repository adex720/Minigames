package io.github.adex720.minigames.discord.command.devcommand;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DevCommandReloadCommands extends DevCommand {

    public DevCommandReloadCommands(MinigamesBot bot) {
        super(bot, "reload-commands");
    }

    @Override
    public boolean onRun(MessageReceivedEvent event) {
        bot.getCommandManager().registerCommands(bot.getJda());
        event.getChannel().sendMessage("Reloaded slash commands.").queue();
        return true;
    }
}
