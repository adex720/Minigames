package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyPrivate  extends Command {

    public CommandPartyPrivate(MinigamesBot bot) {
        super(bot, "party private", "Makes your party require an invite for people to join.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()){
            event.reply("You don't have a party!").queue();
            return true;
        }

        Party party = ci.party();
        long userId = ci.authorId();

        if (party.getId() != userId){
            event.reply("Only the party owner can delete the party.").queue();
            return true;
        }

        party.makePrivate();
        event.reply("You changed the party to private. You now need to invite people for them to be able to join.").queue();

        return true;
    }
}
