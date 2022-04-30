package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.normal.unscramble.MinigameUnscramble;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandUnscrambleSolve extends MinigameSubcommand {

    public CommandUnscrambleSolve(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.UNSCRAMBLE, "solve", "Guesses a word in a game of unscramble.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().UNSCRAMBLE) {
                MinigameUnscramble unscramble = (MinigameUnscramble) minigame;
                unscramble.guess(event,ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing unscramble game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "word", "Word to guess", true);
    }
}
