package io.github.adex720.minigames.discord.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.party.Party;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandPartyInvite extends Subcommand {

    public CommandPartyInvite(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandParty, "invite", "Invites someone to your party.", CommandCategory.PARTY);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.isInParty()) {
            event.getHook().sendMessage("You need to be in a party to invite someone!").queue();
            return true;
        }

        Party party = ci.party();
        User author = ci.author();
        long authorId = author.getIdLong();

        if (party.getOwnerId() != authorId) {
            event.getHook().sendMessage("You need to be the party owner to invite others!").queue();
            return true;
        }

        if (party.isPublic()) {
            event.getHook().sendMessage("People can already join your party! You can make it private with `/party private`").queue();
            return true;
        }

        if (party.isLocked()){
            event.getHook().sendMessage("The party is currently playing a minigame which doesn't allow new users to join the party.").queue();
        }

        if (party.isFull()) {
            event.getHook().sendMessage("You can't invite people to your party because it's full!").queue();
            return true;
        }

        if (party.isFullWithInvites()) {
            event.getHook().sendMessage("You have invited so many people that if they all join your party would become full!").queue();
        }

        User invited = event.getOption("user").getAsUser();
        long invitedId = invited.getIdLong();

        if (party.isInParty(invitedId)) {
            event.getHook().sendMessage(invited.getAsTag() + " is already in your party!").queue();
            return true;
        }

        if (bot.getPartyManager().isInParty(invitedId)) {
            event.getHook().sendMessage(invited.getAsTag() + " is already in a party! Ask them to leave it first.").queue();
            return true;
        }

        if (party.isInvited(invitedId)) {
            event.getHook().sendMessage(invited.getAsTag() + " is already invited to your party. There is no point spamming this command!").queue();
            return true;
        }

        party.invite(invitedId);
        event.getHook().sendMessage(author.getAsMention() + " has invited " + invited.getAsMention() + " to join their party!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.USER, "user", "Member to invite.", true);
    }
}
