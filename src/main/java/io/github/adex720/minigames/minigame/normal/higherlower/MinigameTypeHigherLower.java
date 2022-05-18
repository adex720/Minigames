package io.github.adex720.minigames.minigame.normal.higherlower;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandHigherLowerGuess;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeHigherLower extends MinigameType<MinigameHigherLower> {

    public MinigameTypeHigherLower(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "higher-lower", false, 1);
    }

    @Override
    public MinigameHigherLower create(SlashCommandInteractionEvent event, CommandInfo ci) {
        return MinigameHigherLower.start(event, ci);
    }

    @Override
    public MinigameHigherLower create(ButtonInteractionEvent event, CommandInfo ci) {
        return MinigameHigherLower.start(event, ci);
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
