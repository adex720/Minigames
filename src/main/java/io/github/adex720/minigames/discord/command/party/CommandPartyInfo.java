package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyInfo extends Subcommand {

    public CommandPartyInfo(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "info", "Shows information about your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.reply("You aren't in a party").queue();
            return true;
        }

        Party party = ci.party();

        event.replyEmbeds(party.getInfo(ci.author())).queue();
        return true;
    }
}
