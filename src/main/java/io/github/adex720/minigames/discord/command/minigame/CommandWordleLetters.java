package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.wordle.MinigameWordle;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author adex720
 */
public class CommandWordleLetters extends MinigameSubcommand {

    public CommandWordleLetters(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.WORDLE, "letters", "Sends an image of the letters with correct color backgrounds.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame == null) { // no minigame
            event.getHook().sendMessage("You don't have an ongoing wordle game!").queue();
            return true;
        }

        if (minigame.getType() != bot.getMinigameTypeManager().WORDLE) { // wrong minigame
            event.getHook().sendMessage("You don't have an ongoing wordle game!").queue();
            return true;
        }

        MinigameWordle wordle = (MinigameWordle) minigame;

        File image = new File("wordle.png");
        try {
            ImageIO.write(wordle.getLetters(), "png", image);
            event.getHook().sendMessage("Letters on the word:").addFile(image).queue();
        } catch (IOException e) {
            bot.getLogger().error(e.getMessage());
            return false;
        }
        return true;
    }
}
