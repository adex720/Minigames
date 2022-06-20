package io.github.adex720.minigames.minigame.duel.tictactoe;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.minigame.duel.DuelMinigame;
import io.github.adex720.minigames.minigame.duel.MinigameAI;

import java.util.ArrayList;
import java.util.Random;

/**
 * A very simple AI to play tic-tac-toe.
 * The AI works on three steps.
 * <pl>
 * <li>First, check if AI can win.</li>
 * <li>Second, block any possible straight wins for opponent.</li>
 * <li>Lastly, if this step is reached, place the mark at a random position while giving double weight for corner and the middle tiles.</li>
 * </pl>
 *
 * @author adex720
 */
public class TicTacToeAI extends MinigameAI<MinigameTicTacToe> {

    public TicTacToeAI(MinigamesBot bot) {
        super(bot);
    }

    @Override
    public void makeMove(Random random, DuelMinigame minigame) {
        MinigameTicTacToe minigameTicTacToe = (MinigameTicTacToe) minigame;
        if (aiCheckForWins(minigameTicTacToe)) return;

        aiSetWeightedPosition(minigameTicTacToe, random);
    }

    /**
     * Makes the AI win if it can.
     * If AI can't win this round it proceeds to block any places where whe opponent could win this round.
     *
     * @return true, if a mark was placed, false when not.
     */
    private boolean aiCheckForWins(MinigameTicTacToe minigame) {
        short block = -1;

        for (int line = MinigameTicTacToe.ROW_TOP; line <= MinigameTicTacToe.DIAGONAL_RIGHT_TOP; line++) { // checking if there's a line with 2 same marks and 1 empty
            short score = minigame.getLineScore(line);

            if (score == -2) {
                short place = minigame.getFirstFreeOnLine(line);
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

    /**
     * @return true if a mark was placed, false if board is full.
     */
    private boolean aiSetWeightedPosition(MinigameTicTacToe minigame, Random random) {
        ArrayList<Short> weightedPositions = new ArrayList<>();

        for (short x = 0; x < 3; x++) { // getting random free spot with places on diagonal lines having double change.
            for (short y = 0; y < 3; y++) {
                if (minigame.isOccupied(x, y)) continue;

                short place = MinigameTicTacToe.compact(x, y);
                weightedPositions.add(place);
                if ((x + y) % 2 == 0) {
                    weightedPositions.add(place);
                }
            }
        }

        if (weightedPositions.isEmpty()) return false;

        short place = weightedPositions.get(random.nextInt(weightedPositions.size()));
        minigame.place(MinigameTicTacToe.getXFromCompacted(place), MinigameTicTacToe.getYFromCompacted(place), false);
        minigame.lastAIMove = MinigameTicTacToe.getPositionName(place);

        return true;
    }
}
