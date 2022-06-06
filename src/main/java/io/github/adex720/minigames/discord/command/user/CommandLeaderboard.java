package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.PageCommand;
import io.github.adex720.minigames.gameplay.manager.stat.Leaderboard;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
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

import java.util.Date;

/**
 * @author adex720
 */
public class CommandLeaderboard extends Command implements PageCommand {

    public static final int PER_PAGE = 10;

    public CommandLeaderboard(MinigamesBot bot) {
        super(bot, "leaderboard", "Shows the leaderboard of a category.", CommandCategory.USER);
        requiresProfile();
        registerPageId(bot);
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        int categoryId = event.getOption("category").getAsInt();

        int page = 1;
        int lastPage = 99;
        // This is only used when calculating the amount of entries on the last page
        // -> doesn't need to be calculated when the page is the first one.

        Leaderboard leaderboard = bot.getStatManager().getLeaderboard(categoryId);
        int entriesAmount = leaderboard.size();

        OptionMapping pageOptionMapping = event.getOption("page");
        if (pageOptionMapping != null) {
            page = pageOptionMapping.getAsInt(); // get page from argument

            if (page <= 0) {
                event.getHook().sendMessage("Page must be at least 1!").queue();
                return true;
            }

            lastPage = 1 + (entriesAmount - 1) / PER_PAGE;
        }

        Replyable replyable = Replyable.from(event);

        if (page > lastPage) {
            replyable.reply("Page is outside leaderboard. Last page is " + page + ".");
            return true;
        }

        String categoryName = bot.getStatManager().get(categoryId).name();

        String ranks = getEntries(leaderboard, page, categoryName, lastPage, entriesAmount);
        int userRank = leaderboard.getRank(ci.profile());

        sendLeaderboard(replyable, ci, categoryName, categoryId, userRank, ranks, page, entriesAmount);

        return true;
    }

    @Override
    public void onPageMove(ButtonInteractionEvent event, CommandInfo ci, int page, String[] args) {
        event.deferEdit().queue();
        Replyable replyable = Replyable.edit(event);

        int categoryId = Integer.parseInt(args[0]);
        String categoryName = bot.getStatManager().get(categoryId).name();
        Leaderboard leaderboard = bot.getStatManager().getLeaderboard(categoryId);

        int entriesAmount = leaderboard.size();
        int lastPage = 1 + (entriesAmount - 1) / PER_PAGE;

        String ranks = getEntries(leaderboard, page, categoryName, lastPage, entriesAmount);

        int userRank = leaderboard.getRank(ci.profile());

        sendLeaderboard(replyable, ci, categoryName, categoryId, userRank, ranks, page, entriesAmount);
    }

    @Override
    public String getPageName() {
        return name;
    }

    /**
     * Returns entries on given leaderboard from ranks {@param first} to {@param last} (both included).
     * The entries count rank, username and score.
     *
     * @param leaderboard    The leaderboard
     * @param page           Page number
     * @param categoryName   Name of the category
     * @param lastPageNumber Amount of pages on the leaderboard
     * @param entriesAmount  Amount of entries on the leaderboard
     */
    public String getEntries(Leaderboard leaderboard, int page, String categoryName, int lastPageNumber, int entriesAmount) {
        int first = (page - 1) * PER_PAGE; // Calculate ranks of first and last profile on the page
        int last = page * PER_PAGE - 1;

        if (page == lastPageNumber) {
            last = entriesAmount - 1; // Make page end at last entry if page is the last page.
        }

        return getEntries(leaderboard, first, last, categoryName); // Get page as String
    }

    /**
     * Returns entries on given leaderboard from ranks {@param first} to {@param last} (both included).
     * The entries count rank, username and score.
     *
     * @param leaderboard  The leaderboard
     * @param first        Id of the first entry on the page
     * @param last         Id of the last entry on the page
     * @param categoryName Name of the category
     */
    public String getEntries(Leaderboard leaderboard, int first, int last, String categoryName) {
        return leaderboard.toEntryWithTag(first, last - first + 1, categoryName);
    }

    /**
     * Sends a page of leaderboard.
     *
     * @param replyable     The replyable sending or editing the message
     * @param commandInfo   Command Info
     * @param categoryName  Name of the category
     * @param categoryId    Id of the category
     * @param authorRank    Rank of the executor of the command on the leaderboard
     * @param ranks         Entries on the page as String
     * @param pageNumber    Number of the page
     * @param entriesAmount Amount of entries on the leaderboard
     */
    public void sendLeaderboard(Replyable replyable, CommandInfo commandInfo, String categoryName, int categoryId, int authorRank, String ranks, int pageNumber, int entriesAmount) {
        Profile profile = commandInfo.profile();
        int userScore = profile.getStatValue(categoryId); // Get author's score
        User author = commandInfo.author();

        MessageEmbed message = new EmbedBuilder()
                .setTitle("LEADERBOARD")
                .addField(categoryName + " (Your score: " + Util.formatNumber(userScore) + ", rank: #" + authorRank + ")", ranks, false)
                .setColor(Util.getColor(commandInfo.authorId()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();

        Button previous = getButtonForPage(author.getIdLong(), pageNumber - 1, "previous", pageNumber == 1, Integer.toString(categoryId));
        Button next = getButtonForPage(author.getIdLong(), pageNumber + 1, "next", pageNumber * PER_PAGE >= entriesAmount, Integer.toString(categoryId));

        replyable.reply(message, previous, next);
    }

    @Override
    protected SlashCommandData createCommandData() {
        SlashCommandData commandData = super.createCommandData();

        OptionData optionData = new OptionData(OptionType.INTEGER, "category", "Category to show.", true);
        for (Stat stat : bot.getStatManager().getLeaderboardStats()) {
            String name = stat.name();
            optionData.addChoice((char) (name.charAt(0) - 32) + name.substring(1), stat.id());
        }

        commandData.addOptions(optionData)
                .addOption(OptionType.INTEGER, "page", "Page to show.", false);
        return commandData;
    }
}
