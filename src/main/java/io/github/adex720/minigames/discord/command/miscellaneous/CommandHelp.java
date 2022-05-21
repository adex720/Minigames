package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author adex720
 */
public class CommandHelp extends Command {

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

        int commandsAmount = bot.getCommandManager().getCommands(category).size();
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

        sendCommandsFromCategory(Replyable.from(event), commandInfo, category, page, first, last);

        return true;
    }

    /**
     * Page should be checked to be valid before calling this method.
     */
    public void sendCommandsFromCategory(Replyable replyable, CommandInfo commandInfo, CommandCategory category, int page, int first, int last) {
        ArrayList<Command> commands = bot.getCommandManager().getCommands(category);
        User author = commandInfo.author();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("HELP")
                .setColor(Util.getColor(author.getIdLong()));

        for (int i = first; i <= last; i++) {
            Command command = commands.get(i); // Add entries on selected page
            embedBuilder.addField("**" + command.getFullName() + "**", "**Description:** " + command.description, true);
        }

        Button previous = getButtonForPage(page - 1, "previous", page == 1);
        Button next = getButtonForPage(page + 1, "next", last == commands.size());

        replyable.getWebhookMessageAction(embedBuilder
                        .setFooter(author.getName(), author.getAvatarUrl())
                        .setTimestamp(new Date().toInstant())
                        .build())
                .addActionRow(previous, next).queue();
    }

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

    public Button getButtonForPage(int page, String label, boolean disabled) {
        return new ButtonImpl("page-help-" + page, label, ButtonStyle.SECONDARY, disabled, null);
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
