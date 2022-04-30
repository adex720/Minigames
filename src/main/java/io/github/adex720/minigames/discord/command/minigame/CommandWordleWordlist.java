package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.normal.wordle.MinigameWordle;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandWordleWordlist extends MinigameSubcommand {

    public CommandWordleWordlist(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.WORDLE, "wordlist", "Sends list of all valid words matching the given letters. This tool can only be used once per game.");
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
        String letters = event.getOption("letters").getAsString();
        wordle.sendWordList(event, letters);

        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.STRING, "letters", "Replace joker letters with ?. Length must be 5!", true);
    }
}
