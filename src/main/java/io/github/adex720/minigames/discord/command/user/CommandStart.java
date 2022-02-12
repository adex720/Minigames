package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandStart extends Command {

    public CommandStart(MinigamesBot bot) {
        super(bot, "start", "Creates a profile for you.", CommandCategory.USER);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (ci.hasProfile()) {
            event.getHook().sendMessage("You already have a profile!").queue();
            return true;
        }

        bot.getProfileManager().createProfile(ci.authorId());
        event.getHook().sendMessage("You now have a profile. View your profile with `/profile`").queue();

        return true;
    }
}
