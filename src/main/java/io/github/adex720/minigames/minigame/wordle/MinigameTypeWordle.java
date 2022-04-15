package io.github.adex720.minigames.minigame.wordle;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleGuess;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleLetters;
import io.github.adex720.minigames.discord.command.minigame.CommandWordleWordlist;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

public class MinigameTypeWordle extends MinigameType<MinigameWordle> {

    public MinigameTypeWordle(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "wordle", false, 1);
    }

    @Override
    public MinigameWordle create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameWordle.start(event, ci);
    }

    @Override
    public MinigameWordle create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameWordle.start(event, ci);
    }

    @Override
    public MinigameWordle fromJson(JsonObject json) {
        return MinigameWordle.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        Set<Subcommand> subcommands = new HashSet<>();

        subcommands.add(new CommandWordleGuess(bot, typeManager));
        subcommands.add(new CommandWordleLetters(bot, typeManager));
        subcommands.add(new CommandWordleWordlist(bot, typeManager));

        return subcommands;
    }

    @Override
    public String getReplyForInvalidStartState() {
        return "";
    }
}
