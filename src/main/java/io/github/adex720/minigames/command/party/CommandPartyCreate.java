package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.party.Party;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyCreate extends Command {

    public CommandPartyCreate(MinigamesBot bot) {
        super(bot, "party create", "Creates a party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {

        if (ci.isInParty()) {
            event.reply(ci.authorMention() + ", You can't create a party because you are in one").queue();
            return true;
        }

        Party party = new Party(ci.authorId());
        party.onCreate();

        bot.getPartyManager().addParty(ci.authorId(), party);

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("PARTY")
                .addField("Party created", "Others can join the party with `/party join " + ci.authorMention() + "`", false)
                .setColor(Command.SUCCESSFUL)
                .build()).queue();

        return true;
    }
}
