package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandDelete extends Command {

    public CommandDelete(MinigamesBot bot) {
        super(bot, "delete", "Deletes your profile.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        OptionMapping option = event.getOption("code");
        long id = ci.authorId();

        if (option != null) {
            String code = option.getAsString();

            if (bot.getProfileManager().doesDeletionCodeMatch(id, code)) {
                bot.getProfileManager().deleteProfile(id);
                event.getHook().sendMessage("You deleted your profile. This action can't be undone").queue();
                return true;
            }

            event.getHook().sendMessage("Invalid verification code. Make sure the code is put correct or generate a new code with `/delete` with no code.").queue();
            return true;
        }

        event.getHook().sendMessage("Are you sure that you want to delete your profile? This action can't be undone!\nIf you are sure then use `/delete " + bot.getProfileManager().generateDeletionCode(id) + "`").queue();
        return true;
    }

    @Override
    protected CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.STRING, "code", "verification code", false);
    }
}
