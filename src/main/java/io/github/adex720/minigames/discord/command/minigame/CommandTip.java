package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandTip extends Command {

    public CommandTip(MinigamesBot bot) {
        super(bot, "tip", "Shows tips about a minigame.", CommandCategory.MINIGAME);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return false;
    }

    @Override
    protected CommandData createCommandData() {
        CommandData commandData = super.createCommandData();

        OptionData optionData = new OptionData(OptionType.STRING, "minigame", "Minigame to show tips for.", true);
        for (String minigame : bot.getMinigameTypeManager().getTypes()) {
            optionData.addChoice((char) (minigame.charAt(0) - 32) + minigame.substring(1), minigame);
        }

        return commandData.addOptions(optionData);
    }
}
