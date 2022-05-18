package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.normal.wordle.MinigameWordle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.IOException;

/**
 * @author adex720
 */
public class CommandWordleGuess extends MinigameSubcommand {
    public CommandWordleGuess(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.WORDLE, "guess", "Guesses a word in a wordle game.");
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().WORDLE) {
                MinigameWordle wordle = (MinigameWordle) minigame;
                try {
                    wordle.guess(event, ci);
                } catch (IOException e) {
                    bot.getLogger().error(e.getMessage());
                    return false;
                }

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing wordle game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "word", "Word to guess", true);
    }
}
