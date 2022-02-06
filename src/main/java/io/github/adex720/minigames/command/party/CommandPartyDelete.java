package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyDelete extends Command {

    public CommandPartyDelete(MinigamesBot bot) {
        super(bot, "party delete", "Deletes your own party.", CommandCategory.PARTY);
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

        party.onDelete();
        bot.getPartyManager().removeParty(userId);

        event.reply("Successfully deleted the party!").queue();


        return true;
    }
}
