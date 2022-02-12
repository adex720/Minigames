package io.github.adex720.minigames.gameplay.manager.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.minigame.hangman.MinigameHangman;
import io.github.adex720.minigames.minigame.hangman.MinigameTypeHangman;
import io.github.adex720.minigames.minigame.unscramble.MinigameTypeUnscramble;
import io.github.adex720.minigames.minigame.unscramble.MinigameUnscramble;

public class MinigameTypeManager extends Manager {

    public MinigameType<MinigameHangman> HANGMAN;
    public MinigameType<MinigameUnscramble> UNSCRAMBLE;

    public MinigameTypeManager(MinigamesBot bot) {
        super(bot, "minigame-type-manager");
        init();
    }

    private void init() {
        HANGMAN = new MinigameTypeHangman(bot, this);
        HANGMAN.initCommand();
        HANGMAN.createPlayCommand();

        UNSCRAMBLE = new MinigameTypeUnscramble(bot, this);
        UNSCRAMBLE.initCommand();
        UNSCRAMBLE.createPlayCommand();
    }

}
