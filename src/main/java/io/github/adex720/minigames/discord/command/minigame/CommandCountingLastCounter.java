package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.counting.MinigameCounting;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandCountingLastCounter extends MinigameSubcommand {

    public CommandCountingLastCounter(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.COUNTING, "last-counter", "Tells who counted last time.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().COUNTING) {
                MinigameCounting counting = (MinigameCounting) minigame;

                event.getHook().sendMessage("The last user who counted is <@!" + counting.getLastCounter() + ">.").queue();
                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing counting game!").queue();
        return true;
    }
}
