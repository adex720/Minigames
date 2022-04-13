package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CommandHelp extends Command {

    public static final int COMMANDS_PER_PAGE = 6;

    public CommandHelp(MinigamesBot bot) {
        super(bot, "help", "Sends list of commands with descriptions.", CommandCategory.MISCELLANEOUS);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        CommandManager commandManager = bot.getCommandManager();

        OptionMapping category = event.getOption("category");
        User author = ci.author();

        if (category != null) {
            return sendCommandsFromCategory(event, commandManager.getCategory(category.getAsString()), author);
        }
        return sendCategories(event, commandManager, author);
    }

    private boolean sendCommandsFromCategory(SlashCommandEvent event, CommandCategory category, User author) {
        OptionMapping pageOptionMapping = event.getOption("page");
        int page = pageOptionMapping != null ? (int) pageOptionMapping.getAsLong() : 1;

        if (page <= 0) {
            event.getHook().sendMessage("Page must be at least 1.").queue();
            return true;
        }

        ArrayList<Command> commands = bot.getCommandManager().getCommands(category);
        int commandsAmount = commands.size();
        int max = 1 + (commandsAmount - 1) / COMMANDS_PER_PAGE; // Calculate last page with entries

        if (page > max) { // Check if page is out of range
            event.getHook().sendMessage("Max page for category " + category.name().toLowerCase(Locale.ROOT) + " is " + max + ".").queue();
            return true;
        }

        int first = (page - 1) * COMMANDS_PER_PAGE; // Calculate ranks shown on selected page
        int last = page * COMMANDS_PER_PAGE - 1;

        if (page == max) {
            last = commandsAmount - 1;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("HELP")
                .setColor(Util.getColor(author.getIdLong()));

        for (int i = first; i <= last; i++) {
            Command command = commands.get(i); // Add entries on selected page
            embedBuilder.addField(command.name, "**Description:** " + command.description, true);
        }

        event.getHook().sendMessageEmbeds(embedBuilder
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();

        return true;
    }

    public boolean sendCategories(SlashCommandEvent event, CommandManager commandManager, User author) {
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
    protected CommandData createCommandData() {
        return super.createCommandData()
                .addOptions(new OptionData(OptionType.STRING, "category", "Category to filter commands with.", false)
                        .addChoice("Party", "party")
                        .addChoice("Minigame", "minigame")
                        .addChoice("User", "user")
                        .addChoice("Miscellaneous", "miscellaneous"))
                .addOption(OptionType.INTEGER, "page", "Page to load command from.", false);
    }
}
