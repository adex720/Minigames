package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandPartyJoin extends Subcommand {

    public CommandPartyJoin(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "join", "Joins a party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (ci.isInParty()) {
            event.getHook().sendMessage("You can't join a party because you already are in one.").queue();
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
                    event.getHook().sendMessage("The user is not in a party").queue();
                    return true;
                }
            } else {
                event.getHook().sendMessage("The user is not in a party").queue();
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

        event.getHook().sendMessage("You successfully joined the party.").queue();

        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "party", "A member of the party", true);
    }
}
