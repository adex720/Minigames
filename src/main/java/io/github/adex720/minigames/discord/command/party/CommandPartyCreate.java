package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandPartyCreate extends Subcommand {

    public CommandPartyCreate(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "create", "Creates a party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {

        if (ci.isInParty()) {
            event.getHook().sendMessage("You can't create a party because you are in one").queue();
            return true;
        }

        bot.getPartyManager().createParty(ci.authorId());
        ci.profile().partyJoined(ci.authorId());

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("PARTY")
                .addField("Party created", "Others can join the party with /party join " + ci.authorMention(), false)
                .setColor(Command.SUCCESSFUL)
                .build()).queue();

        return true;
    }
}
