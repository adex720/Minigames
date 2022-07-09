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
public class CommandGuildTransfer extends Subcommand {

    public CommandGuildTransfer(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "transfer", "Transfers the guild ownership.", CommandCategory.GUILD);
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
            event.getHook().sendMessage("Only the guild owner can delete the guild!").setEphemeral(true).queue();
            return true;
        }

        User newOwner = event.getOption("member").getAsUser();
        long newOwnerId = newOwner.getIdLong();

        if (!guild.isInGuild(newOwnerId)) { // No bot data also directs here
            event.getHook().sendMessage("The selected user is not in your guild!").setEphemeral(true).queue();
            return true;
        }

        long previousOwnerId = ci.authorId();

        if (newOwnerId == previousOwnerId) {
            event.getHook().sendMessage("You can't transfer the guild to yourself!").setEphemeral(true).queue();
            return true;
        }

        bot.getGuildManager().transfer(previousOwnerId, newOwnerId, newOwner.getAsTag(), guild.getJoinTimestamp(newOwnerId));
        event.getHook().sendMessage("The guild was transferred successfully!\n" + guild.getName() + " is now owned by " + newOwner.getAsMention() + ".").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "member", "New owner", true);
    }
}
