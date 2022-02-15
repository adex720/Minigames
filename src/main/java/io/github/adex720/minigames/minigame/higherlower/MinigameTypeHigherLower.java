package io.github.adex720.minigames.minigame.higherlower;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandHigherLowerGuess;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

public class MinigameTypeHigherLower extends MinigameType<MinigameHigherLower> {

    public MinigameTypeHigherLower(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "higher-lower", false, 1);
    }

    @Override
    public MinigameHigherLower create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameHigherLower.start(event, ci);
    }

    @Override
    public MinigameHigherLower create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameHigherLower.start(event, ci);
    }

    @Override
    public MinigameHigherLower fromJson(JsonObject json) {
        return MinigameHigherLower.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        Set<Subcommand> subcommands = new HashSet<>();

        subcommands.add(new CommandHigherLowerGuess(bot, typeManager));

        return subcommands;
    }
}
