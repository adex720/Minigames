package io.github.adex720.minigames.minigame.duel.tictactoe;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author adex720
 */
public class TicTacToeButtonManager extends ButtonManager {

    public TicTacToeButtonManager(MinigamesBot bot) {
        super(bot, "tictactoe", true);
    }

    @Override
    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo commandInfo, String[] args) {
        long gameId = Long.parseLong(args[1]);

        if (gameId != commandInfo.gameId()) return;

        Minigame minigame = commandInfo.minigame();
        if (minigame == null || minigame.getType() != bot.getMinigameTypeManager().TIC_TAC_TOE) return;

        MinigameTicTacToe ticTacToe = (MinigameTicTacToe) minigame;
        Replyable replyable = Replyable.from(event);

        short x = switch (args[2].charAt(0)) {
            case '0' -> (short) 0;
            case '1' -> (short) 1;
            default -> (short) 2;
        };

        short y = switch (args[2].charAt(1)) {
            case '0' -> (short) 0;
            case '1' -> (short) 1;
            default -> (short) 2;
        };

        event.deferReply().queue();
        ticTacToe.set(replyable, commandInfo, x, y);
    }
}
