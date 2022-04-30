package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.party.memo.MinigameMemo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;

/**
 * @author adex720
 */
public class CommandMemoTurn extends MinigameSubcommand {

    public CommandMemoTurn(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.MEMO, "turn", "Turns 2 tiles on a game of memo.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().MEMO) {
                MinigameMemo memo = (MinigameMemo) minigame;
                try {
                    memo.turn(event, ci);
                } catch (IOException e) {
                    bot.getLogger().error(e.getMessage());
                    return false;
                }

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing memo game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        Command.Choice[] oneToEight = new Command.Choice[]{
                new Command.Choice("1", 0),
                new Command.Choice("2", 1),
                new Command.Choice("3", 2),
                new Command.Choice("4", 3),
                new Command.Choice("5", 4),
                new Command.Choice("6", 5),
                new Command.Choice("7", 6),
                new Command.Choice("8", 7),
        };

        Command.Choice[] oneToTen = new Command.Choice[]{
                new Command.Choice("1", 0),
                new Command.Choice("2", 1),
                new Command.Choice("3", 2),
                new Command.Choice("4", 3),
                new Command.Choice("5", 4),
                new Command.Choice("6", 5),
                new Command.Choice("7", 6),
                new Command.Choice("8", 7),
                new Command.Choice("9", 8),
                new Command.Choice("10", 9),
        };

        OptionData row = new OptionData(OptionType.INTEGER, "row", "Row of the the card", true)
                .addChoices(oneToEight);
        OptionData column = new OptionData(OptionType.INTEGER, "column", "Column of the the card", true)
                .addChoices(oneToTen);

        return super.getSubcommandData().addOptions(row, column);
    }
}
