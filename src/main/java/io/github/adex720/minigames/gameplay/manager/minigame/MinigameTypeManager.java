package io.github.adex720.minigames.gameplay.manager.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.minigame.hangman.MinigameHangman;
import io.github.adex720.minigames.minigame.hangman.MinigameTypeHangman;
import io.github.adex720.minigames.minigame.higherlower.MinigameHigherLower;
import io.github.adex720.minigames.minigame.higherlower.MinigameTypeHigherLower;
import io.github.adex720.minigames.minigame.tictactoe.MinigameTicTacToe;
import io.github.adex720.minigames.minigame.tictactoe.MinigameTypeTicTacToe;
import io.github.adex720.minigames.minigame.unscramble.MinigameTypeUnscramble;
import io.github.adex720.minigames.minigame.unscramble.MinigameUnscramble;
import io.github.adex720.minigames.minigame.wordle.MinigameTypeWordle;
import io.github.adex720.minigames.minigame.wordle.MinigameWordle;

import java.util.ArrayList;
import java.util.Locale;

public class MinigameTypeManager extends Manager {

    private final ArrayList<String> types;

    public MinigameType<MinigameHangman> HANGMAN;
    public MinigameType<MinigameUnscramble> UNSCRAMBLE;
    public MinigameType<MinigameHigherLower> HIGHER_OR_LOWER;
    public MinigameType<MinigameTicTacToe> TIC_TAC_TOE;
    public MinigameType<MinigameWordle> WORDLE;

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
    }

    private void initCommand(MinigameType<?> minigameType) {
        minigameType.initCommand();
        minigameType.createPlayCommand();
        bot.getCommandManager().addCommand(minigameType.getCommand());
        types.add(minigameType.name);
    }

    public MinigameType<? extends Minigame> getType(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) { // simply faster
            case "hangman" -> HANGMAN;
            case "unscramble" -> UNSCRAMBLE;
            case "higher-lower" -> HIGHER_OR_LOWER;
            case "tic-tac-toe" -> TIC_TAC_TOE;
            case "wordle" -> WORDLE;
            default -> throw new IllegalStateException("Unexpected minigame type: " + name);
        };
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}
