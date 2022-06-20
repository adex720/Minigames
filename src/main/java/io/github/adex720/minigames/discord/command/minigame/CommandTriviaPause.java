package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.party.trivia.MinigameTrivia;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandTriviaPause extends MinigameSubcommand {

    public CommandTriviaPause(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.TRIVIA, "pause", "Pauses a trivia game.");
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().TRIVIA) {
                MinigameTrivia trivia = (MinigameTrivia) minigame;

                if (trivia.isPaused()){
                    event.getHook().sendMessage("The trivia is already paused.").queue();
                    return true;
                }

                trivia.pause();
                event.getHook().sendMessage("New questions won't be asked before the game is unpaused.").queue();
                return true;
            }
        }
        event.getHook().sendMessage("You are not part of a trivia game!").queue();
        return true;
    }
}
