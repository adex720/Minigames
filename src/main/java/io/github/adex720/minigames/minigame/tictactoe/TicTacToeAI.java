package io.github.adex720.minigames.minigame.tictactoe;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.minigame.duel.DuelMinigame;
import io.github.adex720.minigames.minigame.duel.MinigameAI;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TicTacToeAI extends MinigameAI<MinigameTicTacToe> {

    public TicTacToeAI(MinigamesBot bot) {
        super(bot);
    }

    @Override
    public void makeMove(DuelMinigame minigame) {
        MinigameTicTacToe minigameTicTacToe = (MinigameTicTacToe) minigame;
        if (aiCheckForWins(minigameTicTacToe)) return;

        aiSetWeightedPosition(minigameTicTacToe);
    }

    private boolean aiCheckForWins(MinigameTicTacToe minigame) {
        int block = -1;

        for (int line = MinigameTicTacToe.ROW_TOP; line <= MinigameTicTacToe.DIAGONAL_RIGHT_TOP; line++) { // checking if there's a line with 2 same marks and 1 empty
            int score = minigame.getLineScore(line);

            if (score == -2) {
                int place = minigame.getFirstFreeOnLine(line);
                minigame.place(MinigameTicTacToe.getXFromCompacted(place), MinigameTicTacToe.getYFromCompacted(place), false);
                minigame.lastAIMove = MinigameTicTacToe.getPositionName(place);
                return true;
            } else if (score == 2) {
                block = minigame.getFirstFreeOnLine(line);
            }
        }

        if (block >= 0) {
            minigame.place(MinigameTicTacToe.getXFromCompacted(block), MinigameTicTacToe.getYFromCompacted(block), false);
            minigame.lastAIMove = MinigameTicTacToe.getPositionName(block);
            return true;
        }

        return false;
    }

    private boolean aiSetWeightedPosition(MinigameTicTacToe minigame) {
        ArrayList<Integer> weightedPositions = new ArrayList<>();

        for (int x = 0; x < 3; x++) { // getting random free spot with places on diagonal lines having double change.
            for (int y = 0; y < 3; y++) {
                if (minigame.isFree(x, y)) {
                    int place = MinigameTicTacToe.compact(x, y);
                    weightedPositions.add(place);
                    if ((x + y) % 2 != 0) {
                        weightedPositions.add(place);
                    }
                }
            }
        }

        int place = weightedPositions.get(ThreadLocalRandom.current().nextInt(weightedPositions.size()));
        minigame.place(MinigameTicTacToe.getXFromCompacted(place), MinigameTicTacToe.getYFromCompacted(place), false);
        minigame.lastAIMove = MinigameTicTacToe.getPositionName(place);

        return true;
    }
}
