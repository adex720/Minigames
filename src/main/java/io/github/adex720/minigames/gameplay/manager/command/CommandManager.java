package io.github.adex720.minigames.gameplay.manager.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.devcommand.*;
import io.github.adex720.minigames.discord.command.minigame.*;
import io.github.adex720.minigames.discord.command.miscellaneous.*;
import io.github.adex720.minigames.discord.command.party.*;
import io.github.adex720.minigames.discord.command.user.*;
import io.github.adex720.minigames.discord.command.user.booster.CommandBoosters;
import io.github.adex720.minigames.discord.command.user.booster.CommandUse;
import io.github.adex720.minigames.discord.command.user.crate.*;
import io.github.adex720.minigames.discord.listener.DevCommandListener;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This class contains all commands the bot has.
 *
 * @author adex720
 */
public class CommandManager extends Manager {

    public final ArrayList<Command> MAIN_COMMANDS;
    public final ArrayList<Command> SUBCOMMANDS;

    // Parent commands must be declared as their own variables,
    // so they can be given as parameters for subcommands.
    public final CommandParty parentCommandParty;
    public final CommandPlay parentCommandPlay;

    // Some commands must be declared as their own variables,
    // because they need to be accessed elsewhere
    public final CommandUptime commandUptime;

    public final CommandSettings commandSettings;

    public CommandManager(MinigamesBot bot) {
        super(bot, "command_manager");
        MAIN_COMMANDS = new ArrayList<>();
        SUBCOMMANDS = new ArrayList<>();

        parentCommandParty = new CommandParty(bot);
        parentCommandPlay = new CommandPlay(bot);

        commandUptime = new CommandUptime(bot);
        commandSettings = new CommandSettings(bot);
    }

    /**
     * Initializes each command expect minigame specific subcommands
     */
    public void initCommands(MinigamesBot bot) {
        MAIN_COMMANDS.add(new CommandHelp(bot));
        MAIN_COMMANDS.add(new CommandInvite(bot));
        MAIN_COMMANDS.add(new CommandPing(bot));
        MAIN_COMMANDS.add(new CommandServer(bot));
        MAIN_COMMANDS.add(new CommandGithub(bot));
        MAIN_COMMANDS.add(new CommandSuggest(bot));
        MAIN_COMMANDS.add(new CommandReportBug(bot));
        MAIN_COMMANDS.add(new CommandUsage(bot));
        MAIN_COMMANDS.add(new CommandWebsite(bot));
        MAIN_COMMANDS.add(commandUptime);

        MAIN_COMMANDS.add(new CommandProfile(bot));
        MAIN_COMMANDS.add(new CommandStart(bot));
        MAIN_COMMANDS.add(new CommandDelete(bot));
        MAIN_COMMANDS.add(new CommandStats(bot));
        MAIN_COMMANDS.add(new CommandQuests(bot));

        MAIN_COMMANDS.add(commandSettings);

        MAIN_COMMANDS.add(new CommandCrates(bot));
        MAIN_COMMANDS.add(new CommandOpen(bot));
        MAIN_COMMANDS.add(new CommandBoosters(bot));
        MAIN_COMMANDS.add(new CommandUse(bot));
        MAIN_COMMANDS.add(new CommandCooldowns(bot));
        MAIN_COMMANDS.add(new CommandClaim(bot));
        MAIN_COMMANDS.add(new CommandBalance(bot));

        MAIN_COMMANDS.add(new CommandLeaderboard(bot));

        MAIN_COMMANDS.add(new KitCommand(bot, "hourly", KitCommand.NO_COINS, CrateType.UNCOMMON.id, 1));
        MAIN_COMMANDS.add(new KitCommand(bot, "coiner", 5000, KitCommand.NO_CRATE, 4));
        MAIN_COMMANDS.add(new KitCommand(bot, "daily", 30000, KitCommand.NO_CRATE, 24));
        MAIN_COMMANDS.add(new KitCommand(bot, "weekly", KitCommand.NO_COINS, CrateType.LEGENDARY.id, 24 * 7));
        MAIN_COMMANDS.add(new KitCommand(bot, "supporter", "An extra 15 000 coins for members of the support server", 15000, KitCommand.NO_CRATE, 24, KitCommand.Criterion.IN_SUPPORT_SERVER));

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
        SUBCOMMANDS.add(new CommandPartyHelp(bot));

        MAIN_COMMANDS.add(parentCommandPlay);
        MAIN_COMMANDS.add(new CommandMinigameInfo(bot));
        MAIN_COMMANDS.add(new CommandMinigameRules(bot));
        MAIN_COMMANDS.add(new CommandTip(bot));
        MAIN_COMMANDS.add(new CommandQuit(bot));

        initDevCommands(bot);
    }

    /**
     * Registers each dev command.
     */
    private void initDevCommands(MinigamesBot bot) {
        DevCommandListener devCommandListener = bot.getDevCommandListener();

        devCommandListener.addCommand(new DevCommandSave(bot));
        devCommandListener.addCommand(new DevCommandReloadData(bot));

        devCommandListener.addCommand(new DevCommandShutdown(bot));
        devCommandListener.addCommand(new DevCommandTerminate(bot));

        devCommandListener.addCommand(new DevCommandReloadCommands(bot));

        devCommandListener.addCommand(new DevCommandUpdateLeaderboards(bot));
        devCommandListener.addCommand(new DevCommandUpdateLineCount(bot));

        devCommandListener.addCommand(new DevCommandBan(bot));
        devCommandListener.addCommand(new DevCommandUnban(bot));

        devCommandListener.addCommand(new DevCommandBadgeEveryone(bot));
    }

    public void addCommand(Command command) {
        MAIN_COMMANDS.add(command);
    }

    public void addSubcommand(Subcommand subcommand) {
        SUBCOMMANDS.add(subcommand);
    }

    /**
     * Registers each command to Discord.
     */
    public void registerCommands(JDA jda) {
        for (Command command : SUBCOMMANDS) {
            ((Subcommand) command).registerSubcommand(); // Add subcommands
        }

        Set<CommandData> commandData = new HashSet<>();
        MAIN_COMMANDS.forEach(command -> commandData.add(command.commandData));

        jda.updateCommands().addCommands(commandData).queue();

        bot.getLogger().info("Registered all commands");
    }

    public CommandCategory[] getCategories() {
        return CommandCategory.values();
    }

    public CommandCategory getCategory(String name) {
        return CommandCategory.valueOf(name.toUpperCase(Locale.ROOT));
    }

    /**
     * Returns an {@link ArrayList} containing each slash command including subcommands.
     * The commands are on the order registered but subcommands are last.
     */
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

    /**
     * @return Amount off all slash commands including subcommands.
     */
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

    public int getCommandAmount() {
        return MAIN_COMMANDS.size();
    }

    public int getAllCommandAmount() {
        int usableCommands = 0;

        for (Command command : MAIN_COMMANDS) {
            if (!command.isParentCommand()) usableCommands++;
        }

        return usableCommands + SUBCOMMANDS.size();
    }

}
