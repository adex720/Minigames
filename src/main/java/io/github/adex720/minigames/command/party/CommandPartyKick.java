package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPartyKick extends Command {

    public CommandPartyKick(MinigamesBot bot) {
        super(bot, "party kick", "Removes user from your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (ci.isInParty()) {
            event.reply("You aren't in a party.").queue();
            return true;
        }

        long authorId = ci.authorId();
        long kickId = event.getOption("party").getAsUser().getIdLong();

        if (authorId == kickId) {
            event.reply("You can't kick yourself.").queue();
            return true;
        }

        Party party = ci.party();
        if (party.getOwnerId() != authorId) {
            event.reply("You need to be the owner of the party to kick others!").queue();
            return true;
        }

        if (!party.isInParty(kickId)) {
            event.reply("The user you tried to kick isn't in your party.").queue();
            return true;
        }

        party.removeMember(kickId);
        party.onMemberKicked(kickId);
        bot.getProfileManager().getProfile(kickId).partyLeft();

        event.reply(ci.authorMention() + " kicked <@!" + kickId + "> from the party!").queue();

        return true;
    }

    @Override
    public CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "party", "A member of the party to kick.", true);
    }
}
