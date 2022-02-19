package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.stat.Stat;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Date;
import java.util.TreeSet;

public class CommandLeaderboard extends Command {

    public static final int PER_PAGE = 10;

    public CommandLeaderboard(MinigamesBot bot) {
        super(bot, "leaderboard", "Shows the leaderboard of a category.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        int categoryId = (int) event.getOption("category").getAsLong();

        int page = 1;
        OptionMapping pageOptionMapping = event.getOption("page");
        if (pageOptionMapping != null) {
            page = (int) pageOptionMapping.getAsLong();

            if (page <= 0) {
                event.getHook().sendMessage("Page must be at least 1!").queue();
                return true;
            }
        }

        TreeSet<Profile> leaderboard = bot.getStatManager().getLeaderboard(categoryId);
        int amount = leaderboard.size();

        int max = 1 + (amount - 1) / PER_PAGE;
        if (page > max) {
            event.getHook().sendMessage("Page is outside leaderboard. Last page is " + page + ".").queue();
            return true;
        }

        int first = (page - 1) * PER_PAGE;
        int last = page * PER_PAGE - 1;

        if (page == max) {
            last = amount - 1;
        }

        String categoryName = bot.getStatManager().get(categoryId).name();
        StringBuilder leaderboardStringBuilder = new StringBuilder();
        int index = 0;
        for (Profile profile : leaderboard) {
            if (index < first) {
                index++;
                continue;
            }

            leaderboardStringBuilder.append(index + 1).append(". <@!").append(profile.getId()).append(">:\n- ")
                    .append(profile.getStatValue(categoryId)).append(' ').append(categoryName).append('\n');

            if (index == last) break;
            index++;
        }

        int userScore = ci.profile().getStatValue(categoryId);
        User author = ci.author();
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("LEADERBOARD")
                .addField(categoryName + " (Your score: " + userScore + ")", leaderboardStringBuilder.toString(), false)
                .setColor(Util.getColor(ci.authorId()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }

    @Override
    protected CommandData createCommandData() {
        CommandData commandData = super.createCommandData();

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
