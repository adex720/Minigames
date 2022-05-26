package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.Nullable;

/**
 * @author adex720
 */
public class CommandGuildKick extends Subcommand {

    public CommandGuildKick(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "kick", "Kicks a member from your guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Long toKickId = getUserId(event);
        if (toKickId == null) {
            event.getHook().sendMessage("Provide the user!").setEphemeral(true).queue();
            return true;
        }

        Guild guild = ci.guild();
        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        long kickerId = ci.authorId();
        if (!guild.isElderOrOwner(kickerId)) {
            event.getHook().sendMessage("Only guild owner and elders can kick people!").setEphemeral(true).queue();
            return true;
        }

        if (!guild.isInGuild(toKickId)) {
            event.getHook().sendMessage("The user is not in your guild!").setEphemeral(true).queue();
            return true;
        }

        if (kickerId == toKickId) {
            event.getHook().sendMessage("You can't kick yourself!").setEphemeral(true).queue();
            return true;
        }

        if (guild.getId().equals(toKickId)) {
            event.getHook().sendMessage("You can't kick the guild owner!").setEphemeral(true).queue();
            return true;
        }

        if (guild.isElder(toKickId) && guild.getId() != kickerId) {
            event.getHook().sendMessage("You cant' kick the other elders!").setEphemeral(true).queue();
            return true;
        }

        guild.removeMember(toKickId);
        event.getHook().sendMessage("You kicked <@" + toKickId + "> from " + guild.getName() + "!").queue();
        return true;
    }

    /**
     * Returns the id of the user on the arguments.
     * If both arguments are presented, the 'member' argument is used.
     * If both arguments are missing, Null is returned.
     */
    @Nullable
    private Long getUserId(SlashCommandInteractionEvent event) {
        OptionMapping userOptionMapping = event.getOption("member");
        OptionMapping idOptionMapping = event.getOption("id");

        if (userOptionMapping != null) return userOptionMapping.getAsUser().getIdLong();
        if (idOptionMapping != null) return idOptionMapping.getAsLong();

        return null;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "member", "Member to kick", false)
                .addOption(OptionType.INTEGER, "id", "Id of the member", false);
    }
}
