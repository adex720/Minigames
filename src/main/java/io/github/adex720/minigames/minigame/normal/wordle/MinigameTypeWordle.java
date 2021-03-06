package io.github.adex720.minigames.minigame.normal.wordle;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleGuess;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleLetters;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleWordlist;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeWordle extends MinigameType<MinigameWordle> {

    public MinigameTypeWordle(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "wordle", false, 1);
    }

    @Override
    public MinigameWordle create(Replyable replyable, CommandInfo ci) {
        return MinigameWordle.start(replyable, ci);
    }

    @Override
    public MinigameWordle fromJson(JsonObject json) {
        return MinigameWordle.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandWordleGuess(bot, typeManager),
                new CommandWordleLetters(bot, typeManager),
                new CommandWordleWordlist(bot, typeManager));
    }

}
