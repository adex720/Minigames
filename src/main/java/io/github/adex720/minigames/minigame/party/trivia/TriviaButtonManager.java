package io.github.adex720.minigames.minigame.party.trivia;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import io.github.adex720.minigames.minigame.Minigame;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author adex720
 */
public class TriviaButtonManager extends ButtonManager {

    public TriviaButtonManager(MinigamesBot bot) {
        super(bot, "trivia", true);
    }

    @Override
    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo ci, String[] args) {
        long gameId = Long.parseLong(args[1]);
        int answerId = Integer.parseInt(args[2]);

        if (ci.gameId() != gameId) return; // Presser is not part of the competition

        Minigame minigame = bot.getMinigameManager().getMinigame(gameId);

        if (minigame == null || minigame.getType() != bot.getMinigameTypeManager().TRIVIA) return; // Trivia has ended.

        event.deferEdit().queue();
        MinigameTrivia trivia = (MinigameTrivia) minigame;
        trivia.onAnswer(ci.party(), ci.authorId(), answerId);
    }
}
