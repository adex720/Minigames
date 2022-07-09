package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandGuildJoin extends Subcommand {

    public CommandGuildJoin(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "join", "Joins a guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild;
        OptionMapping guildMemberMapping = event.getOption("member");
        OptionMapping guildNameMapping = event.getOption("name");

        if (guildMemberMapping != null) {
            long ownerId = guildMemberMapping.getAsUser().getIdLong();
            guild = bot.getGuildManager().getGuild(ownerId);

            if (guild == null) {
                event.getHook().sendMessage("The chosen user isn't in a guild.").setEphemeral(true).queue();
                return true;
            }
        } else if (guildNameMapping != null) {
            String guildName = guildNameMapping.getAsString();
            guild = bot.getGuildManager().getByName(guildName);

            if (guild == null) {
                event.getHook().sendMessage("No guild matches the name.").setEphemeral(true).queue();
                return true;
            }
        } else {
            event.getHook().sendMessage("Include either the guild name or its member.").setEphemeral(true).queue();
            return true;
        }

        if (guild.isFull()) {
            event.getHook().sendMessage("The guild is full.").setEphemeral(true).queue();
            return true;
        }

        long userId = ci.authorId();
        if (!guild.isPublic() && !guild.isInvited(userId)) {
            event.getHook().sendMessage("The guild is private. Ask the owner or an elder to invite you.").setEphemeral(true).queue();
            return true;
        }

        long timestamp = Util.getEpoch(event);
        guild.addMember(userId, ci.getAuthorTag(), timestamp);
        event.getHook().sendMessage("You joined " + guild.getName() + ".").queue();
        ci.profile().guildJoined(guild.getId());
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "name", "Name of the guild", false)
                .addOption(OptionType.USER, "member", "A member of the guild", false);
    }
}
