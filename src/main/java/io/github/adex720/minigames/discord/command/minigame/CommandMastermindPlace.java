package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.normal.mastermind.MinigameMastermind;
import io.github.adex720.minigames.minigame.normal.mastermind.MinigameTypeMastermind;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandMastermindPlace extends MinigameSubcommand {

    public CommandMastermindPlace(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.MASTERMIND, "place", "Places your guess to te board on a game of Mastermind.");
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().MASTERMIND) {
                MinigameMastermind mastermind = (MinigameMastermind) minigame;
                mastermind.place(event, ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing higher or lower game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {

        return super.getSubcommandData()
                .addOptions(getOptionData("first"),
                        getOptionData("second"),
                        getOptionData("third"),
                        getOptionData("fourth"));
    }

    /**
     * Creates an {@link OptionData} for the given position.
     *
     * @param name ordinal number on lower case letters.
     */
    public OptionData getOptionData(String name) {
        String nameWithCapitalStart = Util.capitalizeFirstLetter(name);

        OptionData optionData = new OptionData(OptionType.INTEGER, name, nameWithCapitalStart + " position for left.", true);
        for (int i = 0; i < 8; i++) {
            optionData.addChoice(Util.capitalizeFirstLetter(MinigameTypeMastermind.COLOR_NAMES[i]), i);
        }
        return optionData;
    }

}
