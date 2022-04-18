package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyPrivate extends Subcommand {

    public CommandPartyPrivate(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "private", "Makes your party require an invite for people to join.", CommandCategory.PARTY);
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
            event.getHook().sendMessage("Only the party owner can delete the party.").queue();
            return true;
        }

        if (party.isLocked()){
            event.getHook().sendMessage("The party status is currently locked from new members because of its current minigame.").queue();
            return true;
        }

        party.makePrivate();
        event.getHook().sendMessage("You changed the party to private. You now need to invite people for them to be able to join.").queue();

        return true;
    }
}
