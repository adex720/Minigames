package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.DecimalFormat;

/**
 * @author adex720
 */
public class CommandUptime extends Command {

    private long started;
    private long online;

    public CommandUptime(MinigamesBot bot) {
        super(bot, "uptime", "Shows bot uptime and system details.", CommandCategory.MISCELLANEOUS);
    }

    // These methods are called from the bot init progress.
    public void botOnline(long time) {
        online = time;
    }

    public void setStarted(long time) {
        started = time;
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        long current = System.currentTimeMillis();
        long uptimeProgram = current - started;
        long uptimeBot = current - online;

        String uptimeProgramFormatted = Util.formatTime((int) (uptimeProgram / 1000)); // Formatting time values
        String uptimeBotFormatted = Util.formatTime((int) (uptimeBot / 1000));


        Runtime runtime = Runtime.getRuntime();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String freeMemory = decimalFormat.format(runtime.freeMemory() / 1048576f); // Getting and formatting runtime values
        String maxMemory = decimalFormat.format(runtime.maxMemory() / 1048576f); // 1 byte = 1048576 megabyte
        int processors = runtime.availableProcessors();

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("UPTIME")
                .addField("Details:", "Progress uptime: " + uptimeProgramFormatted +
                        "\nBot uptime: " + uptimeBotFormatted +
                        "\nFree memory: " + freeMemory + "/" + maxMemory + "mb" +
                        "\nProcessors available: " + processors, false)

                .build()).queue();
        return true;
    }
}
