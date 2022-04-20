package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author adex720
 */
public class CommandMinigameInfo extends Command {

    public CommandMinigameInfo(MinigamesBot bot) {
        super(bot, "minigame-info", "Displays information about a minigame", CommandCategory.MINIGAME);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        String type = event.getOption("minigame").getAsString();

        event.getHook().sendMessage(bot.getMinigameTypeManager().getType(type).description).queue();

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
