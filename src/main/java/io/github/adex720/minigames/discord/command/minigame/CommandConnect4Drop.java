package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.duel.connect4.MinigameConnect4;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandConnect4Drop extends MinigameSubcommand {

    public CommandConnect4Drop(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.CONNECT4, "drop", "Sets your mark on the board on a game of tic tac toe.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().CONNECT4) {
                MinigameConnect4 connect4 = (MinigameConnect4) minigame;
                connect4.drop(event, ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing connect 4 game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOptions(new OptionData(OptionType.INTEGER, "column", "Column to drop the mark.", true).addChoices(getChoices()));
    }

    private Command.Choice[] getChoices() {
        return new Command.Choice[]{new Command.Choice("1", 0),
                new Command.Choice("2", 1),
                new Command.Choice("3", 2),
                new Command.Choice("4", 3),
                new Command.Choice("5", 4),
                new Command.Choice("6", 5),
                new Command.Choice("7", 6)};
    }
}
