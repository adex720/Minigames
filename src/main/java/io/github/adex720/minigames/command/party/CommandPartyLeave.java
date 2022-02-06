package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyLeave extends Command {

    public CommandPartyLeave(MinigamesBot bot) {
        super(bot, "party leave", "Leaves your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {

        if (!ci.isInParty()) {
            event.reply("You aren't in a party.").queue();
            return true;
        }

        Party party = ci.party();
        long userId = ci.authorId();

        if (userId == party.getId()) {
            event.reply("You can't leave your own party. Transfer it to someone else with `/party transfer` or delete it with `/party delete` first.").queue();
            return true;
        }

        party.removeMember(userId);
        party.onMemberLeave(userId);

        ci.profile().partyLeft();

        event.reply("You left your party.").queue();

        return true;
    }
}
