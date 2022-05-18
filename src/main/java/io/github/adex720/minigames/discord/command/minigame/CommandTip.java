package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandTip extends Command {

    public CommandTip(MinigamesBot bot) {
        super(bot, "tip", "Shows tips about a minigame.", CommandCategory.MINIGAME);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        User author = ci.author();
        String minigameName = event.getOption("minigame").getAsString();
        MinigameType<? extends Minigame> minigame = bot.getMinigameTypeManager().getType(minigameName);

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("MINIGAME TIPS")
                .addField("Tips for " + minigameName, minigame.tips, false)
                .setColor(minigame.color)
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }

    @Override
    protected SlashCommandData createCommandData() {
        SlashCommandData commandData = super.createCommandData();

        OptionData optionData = new OptionData(OptionType.STRING, "minigame", "Minigame to show tips for.", true);
        for (String minigame : bot.getMinigameTypeManager().getTypes()) {
            optionData.addChoice((char) (minigame.charAt(0) - 32) + minigame.substring(1), minigame);
        }

        return commandData.addOptions(optionData);
    }
}
