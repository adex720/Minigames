package io.github.adex720.minigames.minigame.memo;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandMemoCards;
import io.github.adex720.minigames.discord.command.minigame.CommandMemoTurn;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeMemo extends PartyCompetitiveMinigameType<MinigameMemo> {

    public MinigameTypeMemo(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "memo", 2);
    }

    @Override
    public MinigameMemo create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameMemo.start(event, ci);
    }

    @Override
    public MinigameMemo create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameMemo.start(event, ci);
    }

    @Override
    public MinigameMemo fromJson(JsonObject json) {
        return MinigameMemo.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandMemoTurn(bot, typeManager),
                new CommandMemoCards(bot, typeManager)
        );
    }
}
