package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.stat.Leaderboard;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandLeaderboard extends Command {

    public static final int PER_PAGE = 10;

    public CommandLeaderboard(MinigamesBot bot) {
        super(bot, "leaderboard", "Shows the leaderboard of a category.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        int categoryId = (int) event.getOption("category").getAsLong();

        int page = 1;
        OptionMapping pageOptionMapping = event.getOption("page");
        if (pageOptionMapping != null) {
            page = (int) pageOptionMapping.getAsLong(); // get page from argument

            if (page <= 0) {
                event.getHook().sendMessage("Page must be at least 1!").queue();
                return true;
            }
        }

        Leaderboard leaderboard = bot.getStatManager().getLeaderboard(categoryId);
        int amount = leaderboard.size();

        int max = 1 + (amount - 1) / PER_PAGE; // TODO: Move this inside previous if statement once more profiles exist
        if (page > max) {
            event.getHook().sendMessage("Page is outside leaderboard. Last page is " + page + ".").queue();
            return true;
        }

        int first = (page - 1) * PER_PAGE; // Calculate ranks of first and last profile on the page
        int last = page * PER_PAGE - 1;

        if (page == max) {
            last = amount - 1; // Make page end at last entry if page is the last page.
        }

        String categoryName = bot.getStatManager().get(categoryId).name();
        String ranks = getEntries(leaderboard, first, last, categoryName); // Get page as String

        Profile profile = ci.profile();
        int userScore = profile.getStatValue(categoryId); // Get authors' score
        User author = ci.author();
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("LEADERBOARD")
                .addField(categoryName + " (Your score: " + Util.formatNumber(userScore) + ", rank: #" + leaderboard.getRank(profile) + ")", ranks, false)
                .setColor(Util.getColor(ci.authorId()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }

    /**
     * @return Entries on given leaderboard from ranks {@param first} to {@param last} (both included).
     * The entries count rank, username and score.
     */
    public String getEntries(Leaderboard leaderboard, int first, int last, String categoryName) {
        return leaderboard.toEntryWithTag(first, last - first + 1, categoryName);
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
