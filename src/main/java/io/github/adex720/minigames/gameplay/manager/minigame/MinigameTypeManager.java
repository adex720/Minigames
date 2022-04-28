package io.github.adex720.minigames.gameplay.manager.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.minigame.counting.MinigameCounting;
import io.github.adex720.minigames.minigame.counting.MinigameTypeCounting;
import io.github.adex720.minigames.minigame.hangman.MinigameHangman;
import io.github.adex720.minigames.minigame.hangman.MinigameTypeHangman;
import io.github.adex720.minigames.minigame.higherlower.MinigameHigherLower;
import io.github.adex720.minigames.minigame.higherlower.MinigameTypeHigherLower;
import io.github.adex720.minigames.minigame.mastermind.MinigameMastermind;
import io.github.adex720.minigames.minigame.mastermind.MinigameTypeMastermind;
import io.github.adex720.minigames.minigame.memo.MinigameMemo;
import io.github.adex720.minigames.minigame.memo.MinigameTypeMemo;
import io.github.adex720.minigames.minigame.tictactoe.MinigameTicTacToe;
import io.github.adex720.minigames.minigame.tictactoe.MinigameTypeTicTacToe;
import io.github.adex720.minigames.minigame.unscramble.MinigameTypeUnscramble;
import io.github.adex720.minigames.minigame.unscramble.MinigameUnscramble;
import io.github.adex720.minigames.minigame.wordle.MinigameTypeWordle;
import io.github.adex720.minigames.minigame.wordle.MinigameWordle;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Contains each minigame type.
 *
 * @author adex720
 */
public class MinigameTypeManager extends Manager {

    private final ArrayList<String> types;

    public MinigameType<MinigameHangman> HANGMAN;
    public MinigameType<MinigameUnscramble> UNSCRAMBLE;
    public MinigameType<MinigameHigherLower> HIGHER_OR_LOWER;
    public MinigameType<MinigameTicTacToe> TIC_TAC_TOE;
    public MinigameType<MinigameWordle> WORDLE;
    public MinigameType<MinigameMastermind> MASTERMIND;

    public MinigameType<MinigameCounting> COUNTING;
    public MinigameType<MinigameMemo> MEMO;

    public MinigameTypeManager(MinigamesBot bot) {
        super(bot, "minigame-type-manager");
        types = new ArrayList<>();

        initAll();
    }

    private void initAll() {
        HANGMAN = new MinigameTypeHangman(bot, this);
        initCommand(HANGMAN);

        UNSCRAMBLE = new MinigameTypeUnscramble(bot, this);
        initCommand(UNSCRAMBLE);

        HIGHER_OR_LOWER = new MinigameTypeHigherLower(bot, this);
        initCommand(HIGHER_OR_LOWER);

        TIC_TAC_TOE = new MinigameTypeTicTacToe(bot, this);
        initCommand(TIC_TAC_TOE);

        WORDLE = new MinigameTypeWordle(bot, this);
        initCommand(WORDLE);

        MASTERMIND = new MinigameTypeMastermind(bot, this);
        initCommand(MASTERMIND);


        COUNTING = new MinigameTypeCounting(bot, this);
        initCommand(COUNTING);

        MEMO = new MinigameTypeMemo(bot, this);
        initCommand(MEMO);
    }

    /**
     * Adds required slash commands for the minigame.
     */
    private void initCommand(MinigameType<?> minigameType) {
        minigameType.initCommand();
        minigameType.createPlayCommand();
        bot.getCommandManager().addCommand(minigameType.getCommand());
        types.add(minigameType.name);
    }

    /**
     * Gets minigame type from its String name.
     */
    public MinigameType<? extends Minigame> getType(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) { // simply faster
            case "hangman" -> HANGMAN;
            case "unscramble" -> UNSCRAMBLE;
            case "higher-lower" -> HIGHER_OR_LOWER;
            case "tic-tac-toe" -> TIC_TAC_TOE;
            case "wordle" -> WORDLE;
            case "mastermind" -> MASTERMIND;

            case "counting" -> COUNTING;
            case "memo" -> MEMO;
            default -> throw new IllegalStateException("Invalid minigame type: " + name);
        };
    }

    /**
     * Returns an {@link ArrayList} containing the name of each minigame type.
     */
    public ArrayList<String> getTypes() {
        return types;
    }
}
