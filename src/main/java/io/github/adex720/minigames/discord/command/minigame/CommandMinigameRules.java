package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandMinigameRules extends Command {

    public CommandMinigameRules(MinigamesBot bot) {
        super(bot, "rules", "Displays rules of a minigame", CommandCategory.MINIGAME);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        String type = event.getOption("minigame").getAsString();

        MinigameType<? extends Minigame> minigameType = bot.getMinigameTypeManager().getType(type);
        event.getHook().sendMessage(minigameType.name + " rules:" + minigameType.rules).queue();

        return true;
    }

    @Override
    protected CommandData createCommandData() {
        OptionData data = new OptionData(OptionType.STRING, "minigame", "Minigame", true);
        for (String minigame : bot.getMinigameTypeManager().getTypes()) {
            data.addChoice(minigame, minigame);
        }

        return super.createCommandData()
                .addOptions(data);
    }
}
