package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandPartyDelete extends Subcommand {

    public CommandPartyDelete(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "delete", "Deletes your own party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {

        if (!ci.isInParty()) {
            event.getHook().sendMessage("You aren't in a party!").queue();
            return true;
        }

        Party party = ci.party();
        long userId = ci.authorId();

        if (party.getId() != userId) {
            event.getHook().sendMessage("Only the party owner can delete the party.").queue();
            return true;
        }

        party.onDelete();
        bot.getPartyManager().removeParty(userId);

        event.getHook().sendMessage("Successfully deleted the party!").queue();


        return true;
    }
}
