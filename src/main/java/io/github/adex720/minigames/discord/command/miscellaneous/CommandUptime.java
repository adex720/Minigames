package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.text.DecimalFormat;

public class CommandUptime extends Command {

    private long started;
    private long online;

    public CommandUptime(MinigamesBot bot) {
        super(bot, "uptime", "Shows bot uptime and system details.", CommandCategory.MISCELLANEOUS);
    }

    public void botOnline(long time) {
        online = time;
    }

    public void setStarted(long time) {
        started = time;
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        long current = System.currentTimeMillis();
        long uptimeProgram = current - started;
        long uptimeBot = current - online;
        String uptimeProgramFormatted = Util.formatTime((int) (uptimeProgram / 1000));
        String uptimeBotFormatted = Util.formatTime((int) (uptimeBot / 1000));


        Runtime runtime = Runtime.getRuntime();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String freeMemory = decimalFormat.format(runtime.freeMemory() / 1048576f);
        String maxMemory = decimalFormat.format(runtime.maxMemory() / 1048576f);
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
