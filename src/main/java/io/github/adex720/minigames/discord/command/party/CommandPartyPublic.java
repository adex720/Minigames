package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyPublic extends Subcommand {

    public CommandPartyPublic(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "public", "Makes it possible for everyone to join your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.reply("You don't have a party!").queue();
            return true;
        }

        Party party = ci.party();
        long userId = ci.authorId();

        if (party.getId() != userId) {
            event.reply("Only the party owner can delete the party.").queue();
            return true;
        }

        party.makePublic();
        event.reply("You changed the party to public. Everyone can now join the party.").queue();

        return true;
    }
}
