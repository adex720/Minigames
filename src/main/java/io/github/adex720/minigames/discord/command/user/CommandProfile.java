package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author adex720
 */
public class CommandProfile extends Command {

    public CommandProfile(MinigamesBot bot) {
        super(bot, "profile", "Views the profile of an user", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        OptionMapping optionMapping = event.getOption("user");

        Profile profile;
        User user;
        if (optionMapping != null) {
            user = optionMapping.getAsUser();

            profile = bot.getProfileManager().getProfile(user.getIdLong());

            if (profile == null) {
                event.getHook().sendMessage("That user doesn't have a profile!").queue();
                return true;
            }
        } else {
            profile = ci.profile();
            user = ci.author();
        }

        event.getHook().sendMessageEmbeds(profile.getProfileEmbed(user, bot)).queue();
        return true;
    }

    @Override
    protected SlashCommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "user", "User to check profile from.", false);
    }
}
