package io.github.adex720.minigames.minigame.normal.unscramble;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandUnscrambleSolve;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeUnscramble extends MinigameType<MinigameUnscramble> {

    public MinigameTypeUnscramble(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "unscramble", false, 1);
    }

    @Override
    public MinigameUnscramble create(Replyable replyable, CommandInfo ci) {
        return MinigameUnscramble.start(replyable, ci);
    }

    @Override
    public MinigameUnscramble fromJson(JsonObject json) {
        return MinigameUnscramble.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandUnscrambleSolve(bot, typeManager));
    }

}
