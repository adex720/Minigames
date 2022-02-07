package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyCreate extends Subcommand {

    public CommandPartyCreate(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "create", "Creates a party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {

        if (ci.isInParty()) {
            event.reply("You can't create a party because you are in one").queue();
            return true;
        }

        Party party = new Party(bot, ci.authorId());
        party.onCreate();

        bot.getPartyManager().addParty(ci.authorId(), party);
        ci.profile().partyJoined(ci.authorId());

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("PARTY")
                .addField("Party created", "Others can join the party with /party join " + ci.authorMention(), false)
                .setColor(Command.SUCCESSFUL)
                .build()).queue();

        return true;
    }
}
