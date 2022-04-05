package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.devcommand.*;
import io.github.adex720.minigames.discord.command.minigame.CommandMinigameInfo;
import io.github.adex720.minigames.discord.command.minigame.CommandPlay;
import io.github.adex720.minigames.discord.command.minigame.CommandQuit;
import io.github.adex720.minigames.discord.command.minigame.CommandTip;
import io.github.adex720.minigames.discord.command.miscellaneous.*;
import io.github.adex720.minigames.discord.command.party.*;
import io.github.adex720.minigames.discord.command.user.*;
import io.github.adex720.minigames.discord.listener.DevCommandListener;
import io.github.adex720.minigames.gameplay.manager.Manager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CommandManager extends Manager {

    private static final boolean SHOULD_RELOAD_COMMANDS = true;

    public final ArrayList<Command> MAIN_COMMANDS;
    public final ArrayList<Command> SUBCOMMANDS;

    public final CommandParty parentCommandParty;
    public final CommandPlay parentCommandPlay;


    public final CommandUptime commandUptime;

    public CommandManager(MinigamesBot bot) {
        super(bot, "command_manager");
        MAIN_COMMANDS = new ArrayList<>();
        SUBCOMMANDS = new ArrayList<>();

        parentCommandParty = new CommandParty(bot);
        parentCommandPlay = new CommandPlay(bot);

        commandUptime = new CommandUptime(bot);
    }

    public void initCommands(MinigamesBot bot) {
        MAIN_COMMANDS.add(new CommandHelp(bot));
        MAIN_COMMANDS.add(new CommandInvite(bot));
        MAIN_COMMANDS.add(new CommandPing(bot));
        MAIN_COMMANDS.add(new CommandServer(bot));
        MAIN_COMMANDS.add(new CommandGithub(bot));
        MAIN_COMMANDS.add(new CommandSuggest(bot));
        MAIN_COMMANDS.add(new CommandReportBug(bot));
        MAIN_COMMANDS.add(commandUptime);

        MAIN_COMMANDS.add(new CommandProfile(bot));
        MAIN_COMMANDS.add(new CommandStart(bot));
        MAIN_COMMANDS.add(new CommandDelete(bot));
        MAIN_COMMANDS.add(new CommandStats(bot));
        MAIN_COMMANDS.add(new CommandQuests(bot));

        MAIN_COMMANDS.add(new CommandCrates(bot));
        MAIN_COMMANDS.add(new CommandOpen(bot));
        MAIN_COMMANDS.add(new CommandBoosters(bot));
        MAIN_COMMANDS.add(new CommandUse(bot));
        MAIN_COMMANDS.add(new CommandCooldowns(bot));
        MAIN_COMMANDS.add(new CommandBalance(bot));

        MAIN_COMMANDS.add(new CommandLeaderboard(bot));

        MAIN_COMMANDS.add(new KitCommand(bot, "hourly", 1000, 1));
        MAIN_COMMANDS.add(new KitCommand(bot, "coiner", 4500, 4));
        MAIN_COMMANDS.add(new KitCommand(bot, "daily", 30000, 24));
        MAIN_COMMANDS.add(new KitCommand(bot, "weekly", 250000, 24 * 7));
        MAIN_COMMANDS.add(new KitCommand(bot, "supporter", "An extra 15 000 coins for members of the support server", 15000, 24, KitCommand.Criterion.IN_SUPPORT_SERVER));

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
        MAIN_COMMANDS.add(new CommandMinigameInfo(bot));
        MAIN_COMMANDS.add(new CommandTip(bot));
        MAIN_COMMANDS.add(new CommandQuit(bot));

        initDevCommands(bot);
    }

    private void initDevCommands(MinigamesBot bot) {
        DevCommandListener devCommandListener = bot.getDevCommandListener();

        devCommandListener.addCommand(new DevCommandSave(bot));
        devCommandListener.addCommand(new DevCommandReloadData(bot));

        devCommandListener.addCommand(new DevCommandShutdown(bot));

        devCommandListener.addCommand(new DevCommandReloadCommands(bot));

        devCommandListener.addCommand(new DevCommandUpdateLeaderboards(bot));
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

    public CommandCategory[] getCategories() {
        return CommandCategory.values();
    }

    public CommandCategory getCategory(String name) {
        return CommandCategory.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public ArrayList<Command> getCommands(CommandCategory category) {
        ArrayList<Command> commands = new ArrayList<>();
        for (Command command : MAIN_COMMANDS) {
            if (command.shouldBeInHelp(category)) {
                commands.add(command);
            }
        }
        for (Command command : SUBCOMMANDS) {
            if (command.shouldBeInHelp(category)) {
                commands.add(command);
            }
        }

        return commands;
    }

    public int getCommandAmount(CommandCategory category) {
        int amount = 0;
        for (Command command : MAIN_COMMANDS) {
            if (command.category == category) {
                amount++;
            }
        }
        for (Command command : SUBCOMMANDS) {
            if (command.category == category) {
                amount++;
            }
        }

        return amount;
    }
}
