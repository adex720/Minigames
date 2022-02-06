package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.profile.Profile;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPartyJoin extends Command {

    public CommandPartyJoin(MinigamesBot bot) {
        super(bot, "party join", "Joins a party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (ci.isInParty()) {
            event.reply("You can't join a party because you already are in one.").queue();
            return true;
        }

        long authorId = ci.authorId();
        long partyId = event.getOption("party").getAsUser().getIdLong();

        Party party = bot.getPartyManager().getParty(partyId);

        if (party == null) {
            Profile checkedProfile = bot.getProfileManager().getProfile(partyId);
            if (checkedProfile != null) {
                if (checkedProfile.isInParty()) {
                    party = bot.getPartyManager().getParty(checkedProfile.getPartyId());
                    partyId = party.getId();
                } else {
                    event.reply("The user is not in a party").queue();
                    return true;
                }
            } else {
                event.reply("The user is not in a party").queue();
                return true;
            }

        }

        if (party.size() < Party.MAX_SIZE) {
            if (!party.isPublic()) {
                if (!party.isInvited(authorId)) {
                    event.reply("This party requires you to be invited before joining. Ask the party owner to invite you with /party invite").queue();
                    return true;
                }
            }
        }

        party.addMember(authorId);
        party.onMemberJoin(authorId);

        ci.profile().partyJoined(partyId);

        event.reply("You successfully joined the party.").queue();

        return true;
    }

    @Override
    public CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "party", "A member of the party", true);
    }
}
