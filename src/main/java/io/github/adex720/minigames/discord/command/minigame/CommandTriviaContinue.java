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
public class CommandTriviaContinue extends MinigameSubcommand {

    public CommandTriviaContinue(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.TRIVIA, "continue", "Continues a paused trivia game.");
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().TRIVIA) {
                MinigameTrivia trivia = (MinigameTrivia) minigame;

                Replyable replyable = Replyable.from(event);
                if (!trivia.continueGame(replyable, ci)) {
                    replyable.reply("The trivia is not paused.");
                }

                return true;
            }
        }
        event.getHook().sendMessage("You are not part of a trivia game!").queue();
        return true;
    }
}
