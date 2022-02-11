package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPartyTransfer extends Subcommand {

    public CommandPartyTransfer(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "transfer", "Transfers the party ownership to another member.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.getHook().sendMessage("You don't have a party!").queue();
            return true;
        }

        Party party = ci.party();
        long userId = ci.authorId();

        if (party.getId() != userId) {
            event.getHook().sendMessage("Only the party owner can transfer the party ownership.").queue();
            return true;
        }

        long newOwnerId = event.getOption("party").getAsUser().getIdLong();
        if (newOwnerId == userId) {
            event.getHook().sendMessage("You are already the owner for the party!").queue();
            return true;
        }

        if (!party.isInParty(newOwnerId)) {
            event.getHook().sendMessage("The new owner must be in the party.").queue();
            return true;
        }

        party.transfer(newOwnerId);
        party.onTransfer(userId);

        event.getHook().sendMessage(ci.authorMention() + " transferred the party to <@!" + newOwnerId + ">!").queue();

        return true;
    }

    @Override
    public CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "party", "The new owner of the guild.", true);
    }
}
