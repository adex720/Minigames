package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.PageCommand;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author adex720
 */
public class CommandHelp extends PageCommand {

    public static final int COMMANDS_PER_PAGE = 6;

    public CommandHelp(MinigamesBot bot) {
        super(bot, "help", "Sends list of commands with descriptions.", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        CommandManager commandManager = bot.getCommandManager();

        OptionMapping category = event.getOption("category");
        User author = ci.author();

        if (category != null) {
            return sendCommandsFromCategory(event, ci, commandManager.getCategory(category.getAsString()));
        }
        return sendCategories(event, commandManager, author);
    }

    private boolean sendCommandsFromCategory(SlashCommandInteractionEvent event, CommandInfo commandInfo, CommandCategory category) {
        OptionMapping pageOptionMapping = event.getOption("page");
        int page = pageOptionMapping != null ? (int) pageOptionMapping.getAsLong() : 1;

        if (page <= 0) {
            event.getHook().sendMessage("Page must be at least 1.").queue();
            return true;
        }

        int commandsAmount = bot.getCommandManager().getCommandAmount(category);
        int lastPage = 1 + (commandsAmount - 1) / COMMANDS_PER_PAGE; // Calculate last page with entries

        if (page > lastPage) { // Check if page is out of range
            event.getHook().sendMessage("Max page for category " + category.name().toLowerCase(Locale.ROOT) + " is " + lastPage + ".").queue();
            return true;
        }

        int first = (page - 1) * COMMANDS_PER_PAGE; // Calculate ranks shown on selected page
        int last = page * COMMANDS_PER_PAGE - 1;

        if (page == lastPage) {
            last = commandsAmount - 1;
        }

        sendCommandsFromCategory(Replyable.from(event), commandInfo, category, page, first, last);

        return true;
    }

    /**
     * Page should be checked to be valid before calling this method.
     */
    public void sendCommandsFromCategory(Replyable replyable, CommandInfo commandInfo, CommandCategory category, int page, int first, int last) {
        ArrayList<Command> commands = bot.getCommandManager().getCommandsForHelp(category);
        User author = commandInfo.author();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("HELP")
                .setColor(Util.getColor(author.getIdLong()));

        for (int i = first; i <= last; i++) {
            Command command = commands.get(i); // Add entries on selected page
            embedBuilder.addField("**/" + command.getFullName() + "**", "**Description:** " + command.description, true);
        }

        Button buttonPrevious = getButtonForPage(page - 1, "previous", page == 1, category.name);
        Button buttonNext = getButtonForPage(page + 1, "next", (page) * 6 >= commands.size(), category.name);

        MessageEmbed message = embedBuilder
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();

        replyable.reply(message, buttonPrevious, buttonNext);
    }

    /**
     * Sends a message containing all categories and amount of commands on them.
     */
    public boolean sendCategories(SlashCommandInteractionEvent event, CommandManager commandManager, User author) {
        StringBuilder categories = new StringBuilder();
        boolean newLine = false;
        for (CommandCategory commandCategory : commandManager.getCategories()) {
            if (newLine) {
                categories.append('\n');
            }
            newLine = true;

            categories.append("- ")
                    .append(commandCategory.name().toLowerCase(Locale.ROOT))
                    .append(": ")
                    .append(commandManager.getCommandAmount(commandCategory))
                    .append(" commands");
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("HELP")
                .addField("Command categories:", categories.toString(), false)
                .setColor(Util.getColor(author.getIdLong()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();

        return true;
    }

    @Override
    public void onPageMove(ButtonInteractionEvent event, CommandInfo ci, String[] args, int page) {
        CommandCategory category = CommandCategory.get(args[3]);

        int commandsAmount = bot.getCommandManager().getCommandAmount(category);

        int first = (page - 1) * COMMANDS_PER_PAGE; // Calculate ranks shown on selected page
        int last = page * COMMANDS_PER_PAGE - 1;

        int lastPage = 1 + (commandsAmount - 1) / COMMANDS_PER_PAGE; // Calculate last page with entries
        if (page == lastPage) {
            last = commandsAmount - 1;
        }

        sendCommandsFromCategory(Replyable.edit(event), ci, category, page, first, last);
    }

    @Override
    protected SlashCommandData createCommandData() {
        return super.createCommandData()
                .addOptions(new OptionData(OptionType.STRING, "category", "Category to filter commands with.", false)
                        .addChoice("Party", "party")
                        .addChoice("Minigame", "minigame")
                        .addChoice("User", "user")
                        .addChoice("Miscellaneous", "miscellaneous"))
                .addOption(OptionType.INTEGER, "page", "Page to load command from.", false);
    }
}
