package io.github.adex720.minigames.minigame.normal.higherlower;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandHigherLowerGuess;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeHigherLower extends MinigameType<MinigameHigherLower> {

    public MinigameTypeHigherLower(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "higher-lower", false, 1);
    }

    @Override
    public MinigameHigherLower create(Replyable replyable, CommandInfo ci) {
        return MinigameHigherLower.start(replyable, ci);
    }

    @Override
    public MinigameHigherLower fromJson(JsonObject json) {
        return MinigameHigherLower.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandHigherLowerGuess(bot, typeManager));
    }
}
