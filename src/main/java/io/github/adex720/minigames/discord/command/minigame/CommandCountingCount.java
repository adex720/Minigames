package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.party.counting.MinigameCounting;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author adex720
 */
public class CommandCountingCount extends MinigameSubcommand {

    public CommandCountingCount(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.COUNTING, "count", "Displays the current count.");
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().COUNTING) {
                MinigameCounting counting = (MinigameCounting) minigame;

                event.getHook().sendMessage("The current count is " + counting.getCurrent() + ".").queue();
                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing counting game!").queue();
        return true;
    }
}
