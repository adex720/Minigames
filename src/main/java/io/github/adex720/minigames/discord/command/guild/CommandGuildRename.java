package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandGuildRename extends Subcommand {

    public CommandGuildRename(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "rename", "Renames your guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        if (!ci.isGuildOwner()) {
            event.getHook().sendMessage("Only the guild owner can rename the guild!").setEphemeral(true).queue();
            return true;
        }

        String newName = event.getOption("name").getAsString();

        if (!Guild.isNameValid(newName)) {
            event.getHook().sendMessage("The name is invalid. (too long or contains illegal characters)").setEphemeral(true).queue();
            return true;
        }

        if (bot.getGuildManager().doesGuildExist(newName)) {
            event.getHook().sendMessage("That name unfortunately is already in use.").setEphemeral(true).queue();
            return true;
        }

        guild.rename(newName);
        event.getHook().sendMessage("Your guild was renamed to " + newName).setEphemeral(true).queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "name", "New name", true);
    }
}
