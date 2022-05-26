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
public class CommandGuildDelete extends Subcommand {

    public CommandGuildDelete(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "delete", "Deletes your guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        if (!event.getOption("confirmation").getAsBoolean()) {
            event.getHook().sendMessage("Action cancelled due to confirmation check fail!").setEphemeral(true).queue();
            return true;
        }

        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        if (!ci.isGuildOwner()) {
            event.getHook().sendMessage("Only the guild owner can delete the guild!").setEphemeral(true).queue();
            return true;
        }

        bot.getGuildManager().remove(guild.getId());
        event.getHook().sendMessage("You deleted your guild " + guild.getName() + "!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.BOOLEAN, "confirmation", "To make sure you don't accidentally delete the guild.", true);
    }
}
