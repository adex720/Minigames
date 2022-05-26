package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandGuildCreate extends Subcommand {

    public CommandGuildCreate(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "create", "Creates a guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        if (ci.isInGuild()) {
            event.getHook().sendMessage("You are already in a guild!").setEphemeral(true).queue();
            return true;
        }

        String guildName = event.getOption("name").getAsString();

        if (!Guild.isNameValid(name)) {
            event.getHook().sendMessage("The name is invalid. (too long or contains illegal characters)").setEphemeral(true).queue();
            return true;
        }

        User user = ci.author();
        bot.getGuildManager().create(user.getIdLong(), user.getAsTag(), guildName);

        event.getHook().sendMessage("You created guild " + guildName).queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "name", "Name of the guild", true);
    }
}
