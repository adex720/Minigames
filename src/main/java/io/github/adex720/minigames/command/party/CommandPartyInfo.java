package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyInfo extends Command {

    public CommandPartyInfo(MinigamesBot bot) {
        super(bot, "party info", "Shows information about your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.reply("You aren't in a party").queue();
            return true;
        }

        Party party = ci.party();

        event.replyEmbeds(party.getInfo(ci.author())).queue();
        return true;
    }
}
