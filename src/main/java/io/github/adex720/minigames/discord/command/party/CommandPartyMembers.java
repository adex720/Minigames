package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyMembers extends Subcommand {

    public CommandPartyMembers(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "members", "Lists the members of your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.getHook().sendMessage("You aren't in a party").queue();
            return true;
        }

        Party party = ci.party();

        event.getHook().sendMessageEmbeds(party.getMembers(ci.author())).queue();
        return true;
    }
}
