package io.github.adex720.minigames.minigame.duel.tictactoe;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandTicTacToeSet;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeTicTacToe extends DuelMinigameType<MinigameTicTacToe> {

    public MinigameTypeTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "tic-tac-toe", new TicTacToeAI(bot));
    }

    @Override
    public MinigameTicTacToe create(Replyable replyable, CommandInfo ci) {
        return MinigameTicTacToe.start(replyable, ci);
    }

    @Override
    public MinigameTicTacToe fromJson(JsonObject json) {
        return MinigameTicTacToe.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandTicTacToeSet(bot, typeManager));
    }
}
