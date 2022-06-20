package io.github.adex720.minigames.minigame.gamble.blackjack;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.button.ButtonManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * @author adex720
 */
public class BlackjackButtonManager extends ButtonManager {

    public BlackjackButtonManager(MinigamesBot bot) {
        super(bot, "blackjack", true);
    }

    @Override
    public void onButtonPressed(ButtonInteractionEvent event, CommandInfo commandInfo, String[] args) {
        String gameId = args[2];

        if (!gameId.equals(commandInfo.authorIdString())) return;

        Minigame minigame = commandInfo.minigame();
        if (minigame == null || minigame.getType() != bot.getMinigameTypeManager().BLACKJACK) return; // Already ended

        MinigameBlackjack blackjack = (MinigameBlackjack) minigame;

        String action = args[1];
        Replyable replyable = Replyable.from(event);
        event.deferReply().queue();
        switch (action) {
            case "hit" -> blackjack.hit(replyable, commandInfo);
            case "stand" -> blackjack.stand(replyable, commandInfo);
            case "double" -> blackjack.hitDouble(replyable, commandInfo);
            default -> bot.getLogger().error("Unexpected blackjack action: {}", action);
        }
    }

}
