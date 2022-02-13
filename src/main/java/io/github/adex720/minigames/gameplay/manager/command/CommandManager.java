package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.devcommand.DevCommandSave;
import io.github.adex720.minigames.discord.command.devcommand.DevCommandShutdown;
import io.github.adex720.minigames.discord.command.minigame.CommandPlay;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandInvite;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandPing;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandServer;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandUptime;
import io.github.adex720.minigames.discord.command.party.*;
import io.github.adex720.minigames.discord.command.user.CommandDelete;
import io.github.adex720.minigames.discord.command.user.CommandStart;
import io.github.adex720.minigames.discord.listener.DevCommandListener;
import io.github.adex720.minigames.gameplay.manager.Manager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashSet;
import java.util.Set;

public class CommandManager extends Manager {

    private static final boolean SHOULD_RELOAD_COMMANDS = true;

    public final Set<Command> MAIN_COMMANDS;
    public final Set<Command> SUBCOMMANDS;

    public final CommandParty parentCommandParty;
    public final CommandPlay parentCommandPlay;


    public final CommandUptime commandUptime;

    public CommandManager(MinigamesBot bot) {
        super(bot, "command_manager");
        MAIN_COMMANDS = new HashSet<>();
        SUBCOMMANDS = new HashSet<>();

        parentCommandParty = new CommandParty(bot);
        parentCommandPlay = new CommandPlay(bot);

        commandUptime = new CommandUptime(bot);
    }

    public void initCommands(MinigamesBot bot) {
        MAIN_COMMANDS.add(new CommandInvite(bot));
        MAIN_COMMANDS.add(new CommandPing(bot));
        MAIN_COMMANDS.add(new CommandServer(bot));
        MAIN_COMMANDS.add(commandUptime);

        MAIN_COMMANDS.add(new CommandStart(bot));
        MAIN_COMMANDS.add(new CommandDelete(bot));

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

        MAIN_COMMANDS.add(parentCommandPlay);
        MAIN_COMMANDS.add(bot.getMinigameTypeManager().HANGMAN.getCommand());
        MAIN_COMMANDS.add(bot.getMinigameTypeManager().UNSCRAMBLE.getCommand());
        MAIN_COMMANDS.add(bot.getMinigameTypeManager().HIGHER_OR_LOWER.getCommand());

        initDevCommands(bot);
    }

    private void initDevCommands(MinigamesBot bot) {
        DevCommandListener devCommandListener = bot.getDevCommandListener();

        devCommandListener.addCommand(new DevCommandSave(bot));
        devCommandListener.addCommand(new DevCommandShutdown(bot));
    }

    public void addCommand(Command command) {
        MAIN_COMMANDS.add(command);
    }

    public void addSubcommand(Subcommand subcommand) {
        SUBCOMMANDS.add(subcommand);
    }

    public void registerCommands(JDA jda) {
        for (Command command : SUBCOMMANDS) {
            ((Subcommand) command).registerSubcommand();
        }
        if (SHOULD_RELOAD_COMMANDS) {

            Set<CommandData> commandData = new HashSet<>();
            MAIN_COMMANDS.forEach(command -> commandData.add(command.commandData));

            jda.updateCommands().addCommands(commandData).queue();

            bot.getLogger().info("Registered all commands");
        }
    }


}
