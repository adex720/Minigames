package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CommandPartyJoin extends Command {

    protected CommandPartyJoin(MinigamesBot bot) {
        super(bot, "party join", "Join a party", CommandCategory.PARTY);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (ci.isInParty()) {
            event.reply(ci.authorMention() + ", You can't join a party because you already are in one.").queue();
            return true;
        }

        long partyId = event.getOption("party").getAsUser().getIdLong();


        return true;
    }

    @Override
    public CommandData createCommandData() {
        return super.createCommandData()
                .addOption(OptionType.USER, "party", "A member of the party", true);
    }
}
