package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPartyInvite  extends Command {

    public CommandPartyInvite(MinigamesBot bot) {
        super(bot, "party invite", "Invites someone to your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return true;
    }

    @Override
    public CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "party", "Member to invite.", true);
    }
}
