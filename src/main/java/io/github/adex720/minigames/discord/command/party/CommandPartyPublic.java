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
public class CommandPartyPublic extends Subcommand {

    public CommandPartyPublic(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "public", "Makes it possible for everyone to join your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
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

        if (party.isLocked()) {
            event.getHook().sendMessage("The party can't be made public because of its current minigame.").queue();
            return true;
        }

        party.makePublic();
        event.getHook().sendMessage("You changed the party to public. Everyone can now join the party.").queue();

        return true;
    }
}
