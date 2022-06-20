package io.github.adex720.minigames.minigame.duel.connect4;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author adex720
 */
public class Connect4ButtonManager extends ButtonManager {

    public Connect4ButtonManager(MinigamesBot bot) {
        super(bot, "connect4");
    }

    @Override
    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo commandInfo, String[] args) {
        long gameId = Long.parseLong(args[1]);

        if (gameId != commandInfo.gameId()) return;

        Minigame minigame = commandInfo.minigame();
        if (minigame == null || minigame.getType() != bot.getMinigameTypeManager().CONNECT4) return;

        MinigameConnect4 connect4 = (MinigameConnect4) minigame;
        Replyable replyable = Replyable.from(event);

        int columnId = Integer.parseInt(args[2]);

        connect4.drop(replyable, commandInfo, columnId);
    }
}
