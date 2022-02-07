package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.ParentCommand;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandInvite;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandPing;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandServer;
import io.github.adex720.minigames.discord.command.party.*;
import io.github.adex720.minigames.gameplay.manager.Manager;
import net.dv8tion.jda.api.JDA;

import java.util.HashSet;
import java.util.Set;

public class CommandManager extends Manager {

    private static final boolean SHOULD_RELOAD_COMMANDS = false;

    public final Set<Command> MAIN_COMMANDS;
    public final Set<Command> SUBCOMMANDS;

    public final ParentCommand parentCommandParty;

    public CommandManager(MinigamesBot bot) {
        super(bot, "command_manager");
        MAIN_COMMANDS = new HashSet<>();
        SUBCOMMANDS = new HashSet<>();

        parentCommandParty = new CommandParty(bot);
    }

    public void initCommands(MinigamesBot bot) {
        MAIN_COMMANDS.add(new CommandInvite(bot));
        MAIN_COMMANDS.add(new CommandPing(bot));
        MAIN_COMMANDS.add(new CommandServer(bot));

        MAIN_COMMANDS.add(parentCommandParty);
        SUBCOMMANDS.add(new CommandPartyCreate(bot));
        SUBCOMMANDS.add(new CommandPartyDelete(bot));
        SUBCOMMANDS.add(new CommandPartyInfo(bot));
        SUBCOMMANDS.add(new CommandPartyInvite(bot));
        SUBCOMMANDS.add(new CommandPartyJoin(bot));
        SUBCOMMANDS.add(new CommandPartyKick(bot));
        SUBCOMMANDS.add(new CommandPartyLeave(bot));
        SUBCOMMANDS.add(new CommandPartyMembers(bot));
        SUBCOMMANDS.add(new CommandPartyPrivate(bot));
        SUBCOMMANDS.add(new CommandPartyPublic(bot));
        SUBCOMMANDS.add(new CommandPartyTransfer(bot));
    }

    public void registerCommands(JDA jda) {
        if (SHOULD_RELOAD_COMMANDS) {

            jda.updateCommands().queue(); // deleting previous commands

            for (Command command : SUBCOMMANDS) {
                ((Subcommand) command).registerSubcommand();
            } // all subcommands should be created before registering the parent command

            for (Command command : MAIN_COMMANDS) {
                jda.upsertCommand(command.getCommandData()).queue();
            }

            bot.getLogger().info("Registered all commands");
        } else {
            for (Command command : SUBCOMMANDS) {
                ((Subcommand) command).registerSubcommand();
            }
        }
    }


}
