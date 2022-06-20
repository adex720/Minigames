package io.github.adex720.minigames.minigame.party.trivia;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandTriviaContinue;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeTrivia extends PartyCompetitiveMinigameType<MinigameTrivia> {

    public MinigameTypeTrivia(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "trivia", 2);
    }

    @Override
    public MinigameTrivia create(Replyable replyable, CommandInfo ci) {
        return MinigameTrivia.start(replyable, ci);
    }

    @Override
    public MinigameTrivia fromJson(JsonObject json) {
        return MinigameTrivia.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandTriviaContinue(bot, typeManager)
                //, new CommandTriviaPause(bot,typeManager)
        );
    }
}
