package io.github.adex720.minigames.gameplay.manager.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.minigame.duel.connect4.MinigameConnect4;
import io.github.adex720.minigames.minigame.duel.connect4.MinigameTypeConnect4;
import io.github.adex720.minigames.minigame.duel.tictactoe.MinigameTicTacToe;
import io.github.adex720.minigames.minigame.duel.tictactoe.MinigameTypeTicTacToe;
import io.github.adex720.minigames.minigame.gamble.GambleMinigameType;
import io.github.adex720.minigames.minigame.gamble.blackjack.MinigameBlackjack;
import io.github.adex720.minigames.minigame.gamble.blackjack.MinigameTypeBlackjack;
import io.github.adex720.minigames.minigame.normal.hangman.MinigameHangman;
import io.github.adex720.minigames.minigame.normal.hangman.MinigameTypeHangman;
import io.github.adex720.minigames.minigame.normal.higherlower.MinigameHigherLower;
import io.github.adex720.minigames.minigame.normal.higherlower.MinigameTypeHigherLower;
import io.github.adex720.minigames.minigame.normal.mastermind.MinigameMastermind;
import io.github.adex720.minigames.minigame.normal.mastermind.MinigameTypeMastermind;
import io.github.adex720.minigames.minigame.normal.unscramble.MinigameTypeUnscramble;
import io.github.adex720.minigames.minigame.normal.unscramble.MinigameUnscramble;
import io.github.adex720.minigames.minigame.normal.wordle.MinigameTypeWordle;
import io.github.adex720.minigames.minigame.normal.wordle.MinigameWordle;
import io.github.adex720.minigames.minigame.party.counting.MinigameCounting;
import io.github.adex720.minigames.minigame.party.counting.MinigameTypeCounting;
import io.github.adex720.minigames.minigame.party.memo.MinigameMemo;
import io.github.adex720.minigames.minigame.party.memo.MinigameTypeMemo;

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
    public MinigameType<MinigameWordle> WORDLE;
    public MinigameType<MinigameMastermind> MASTERMIND;

    public MinigameType<MinigameTicTacToe> TIC_TAC_TOE;
    public MinigameType<MinigameConnect4> CONNECT4;

    public MinigameType<MinigameCounting> COUNTING;
    public MinigameType<MinigameMemo> MEMO;

    public GambleMinigameType<MinigameBlackjack> BLACKJACK;

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

        WORDLE = new MinigameTypeWordle(bot, this);
        initCommand(WORDLE);

        MASTERMIND = new MinigameTypeMastermind(bot, this);
        initCommand(MASTERMIND);


        TIC_TAC_TOE = new MinigameTypeTicTacToe(bot, this);
        initCommand(TIC_TAC_TOE);

        CONNECT4 = new MinigameTypeConnect4(bot, this);
        initCommand(CONNECT4);


        COUNTING = new MinigameTypeCounting(bot, this);
        initCommand(COUNTING);

        MEMO = new MinigameTypeMemo(bot, this);
        initCommand(MEMO);


        BLACKJACK = new MinigameTypeBlackjack(bot, this);
        initCommand(BLACKJACK);
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
            case "higher-lower", "higherlower" -> HIGHER_OR_LOWER;
            case "wordle" -> WORDLE;
            case "mastermind" -> MASTERMIND;

            case "tic-tac-toe", "tictactoe" -> TIC_TAC_TOE;
            case "connect4" -> CONNECT4;

            case "counting" -> COUNTING;
            case "memo" -> MEMO;

            case "blackjack" -> BLACKJACK;
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
