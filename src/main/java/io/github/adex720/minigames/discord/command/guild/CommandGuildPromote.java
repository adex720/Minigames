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
public class CommandGuildPromote extends Subcommand {

    public CommandGuildPromote(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "promote", "Promotes a member to elder.", CommandCategory.GUILD);
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
            event.getHook().sendMessage("Only the guild owner can promote others!").setEphemeral(true).queue();
            return true;
        }

        User promotingUser = event.getOption("member").getAsUser();
        long promotingId = promotingUser.getIdLong();

        if (!guild.isInGuild(promotingId)) { // No bot data also directs here
            event.getHook().sendMessage("The selected user is not in your guild!").setEphemeral(true).queue();
            return true;
        }

        long previousOwnerId = ci.authorId();

        if (promotingId == previousOwnerId) {
            event.getHook().sendMessage("You can't promote yourself!").setEphemeral(true).queue();
            return true;
        }

        if (guild.isElder(promotingId)) {
            event.getHook().sendMessage("The user is already an elder!").setEphemeral(true).queue();
            return true;
        }

        guild.promote(promotingId);
        event.getHook().sendMessage("You promoted " + promotingUser.getAsMention() + " to an elder!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "member", "Member to promote", true);
    }
}
