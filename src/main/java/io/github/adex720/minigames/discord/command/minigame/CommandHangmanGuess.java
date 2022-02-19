package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.hangman.MinigameHangman;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandHangmanGuess extends MinigameSubcommand {

    public CommandHangmanGuess(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.HANGMAN, "guess", "Guesses something in a game of hangman.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().HANGMAN) {
                MinigameHangman hangman = (MinigameHangman) minigame;
                hangman.guess(event, ci);

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing hangman game!").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "guess", "Letter or word to guess", true);
    }
}
