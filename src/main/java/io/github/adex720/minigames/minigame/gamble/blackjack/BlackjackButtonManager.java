package io.github.adex720.minigames.minigame.gamble.blackjack;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

/**
 * @author adex720
 */
public class BlackjackButtonManager {

    private final MinigamesBot bot;

    public BlackjackButtonManager(MinigamesBot bot) {
        this.bot = bot;
    }

    public void onButtonPressed(ButtonClickEvent event, CommandInfo commandInfo, String[] args) {
        String action = args[1];
        String gameId = args[2];

        if (!gameId.equals(commandInfo.authorIdString())) return;

        Minigame minigame = commandInfo.minigame();
        if (minigame.getType() != bot.getMinigameTypeManager().BLACKJACK) return; // Already ended

        MinigameBlackjack blackjack = (MinigameBlackjack) minigame;

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
