package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * @author adex720
 */
public class CommandPartyHelp extends Subcommand {

    public CommandPartyHelp(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "help", "Shows information about parties.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        event.getHook().sendMessage("""
        Party lets you play minigames with your friends (and other people too)!
        When a party completes a minigame everyone gets rewards.
        Some minigames require a party to be played.
        To see list of all party commands use `/help Party`.
        """)
        .queue();
        return true;
    }
}
