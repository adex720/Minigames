package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandPartyInvite extends Subcommand {

    public CommandPartyInvite(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "invite", "Invites someone to your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return true; // TODO: create invite command
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "party", "Member to invite.", true);
    }
}
