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
public class CommandGuildDemote extends Subcommand {

    public CommandGuildDemote(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "demote", "Demotes an elder from your guild.", CommandCategory.GUILD);
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
            event.getHook().sendMessage("Only the guild owner can demote others!").setEphemeral(true).queue();
            return true;
        }

        User demotingUser = event.getOption("member").getAsUser();
        long demotingId = demotingUser.getIdLong();

        if (!guild.isInGuild(demotingId)) { // No bot data also directs here
            event.getHook().sendMessage("The selected user is not in your guild!").setEphemeral(true).queue();
            return true;
        }

        long previousOwnerId = ci.authorId();

        if (demotingId == previousOwnerId) {
            event.getHook().sendMessage("You can't demote yourself!").setEphemeral(true).queue();
            return true;
        }

        if (guild.isElder(demotingId)) {
            event.getHook().sendMessage("The user is already an elder!").setEphemeral(true).queue();
            return true;
        }

        guild.demote(demotingId);
        event.getHook().sendMessage("You demoted " + demotingUser.getAsMention() + "!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "member", "Member to demote", true);
    }
}
