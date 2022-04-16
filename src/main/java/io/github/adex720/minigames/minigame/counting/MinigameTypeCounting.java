package io.github.adex720.minigames.minigame.counting;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

public class MinigameTypeCounting extends PartyMinigameType<MinigameCounting> {

    public MinigameTypeCounting(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "counting", 3);
    }

    @Override
    public MinigameCounting create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameCounting.start(event,ci);
    }

    @Override
    public MinigameCounting create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameCounting.start(event,ci);
    }

    @Override
    public MinigameCounting fromJson(JsonObject json) {
        return MinigameCounting.fromJson(bot, json);
    }

    /**
     * @return Empty Set
     */
    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of();
    }
}
