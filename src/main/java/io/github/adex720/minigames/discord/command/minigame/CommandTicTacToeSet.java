package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.tictactoe.MinigameTicTacToe;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandTicTacToeSet extends MinigameSubcommand {

    public CommandTicTacToeSet(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.TIC_TAC_TOE, "set", "Sets your mark on the board on a game of tic tac toe.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().TIC_TAC_TOE) {
                MinigameTicTacToe hangman = (MinigameTicTacToe) minigame;
                hangman.set(event, ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing tic-tac-toe game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOptions(new OptionData(OptionType.INTEGER, "row", "Row to place the mark.", true).addChoices(getRowChoices()))
                .addOptions(new OptionData(OptionType.INTEGER, "column", "Column to place the mark.", true).addChoices(getColumnChoices()));
    }

    private Command.Choice[] getRowChoices() {
        return new Command.Choice[]{new Command.Choice("top", 0),
                new Command.Choice("middle", 1),
                new Command.Choice("bottom", 2)};
    }

    private Command.Choice[] getColumnChoices() {
        return new Command.Choice[]{new Command.Choice("left", 0),
                new Command.Choice("middle", 1),
                new Command.Choice("right", 2)};
    }
}
