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
public class CommandGuildInvite extends Subcommand {

    public CommandGuildInvite(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "invite", "Invites someone to your guild.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();

        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        long inviterId = ci.authorId();
        if (!guild.isElderOrOwner(inviterId)) {
            event.getHook().sendMessage("Only guild owner and elders can invite people!").setEphemeral(true).queue();
            return true;
        }

        User invitable = event.getOption("user").getAsUser();
        long invitableId = invitable.getIdLong();
        if (guild.isInGuild(invitableId)) {
            event.getHook().sendMessage("The user is already in the guild!").setEphemeral(true).queue();
            return true;
        }

        if (guild.isFull()) {
            event.getHook().sendMessage("Your guild is full!").setEphemeral(true).queue();
            return true;
        }

        if (guild.isFullWithInvites()) {
            event.getHook().sendMessage("You have reached the limit of users on the invite list!").setEphemeral(true).queue();
            return true;
        }

        guild.invite(invitableId);
        event.getHook().sendMessage("You invited " + invitable.getAsMention() + " to join your guild.").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "user", "User to invite", true);
    }
}
