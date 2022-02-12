package io.github.adex720.minigames.minigame.unscramble;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandUnscrambleSolve;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

public class MinigameTypeUnscramble extends MinigameType<MinigameUnscramble> {

    public MinigameTypeUnscramble(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "unscramble", "description", false, 1); // TODO: add description
    }

    @Override
    public MinigameUnscramble create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameUnscramble.start(event, ci);
    }

    @Override
    public MinigameUnscramble create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameUnscramble.start(event, ci);
    }

    @Override
    public MinigameUnscramble fromJson(JsonObject json) {
        return MinigameUnscramble.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        Set<Subcommand> subcommands = new HashSet<>();

        subcommands.add(new CommandUnscrambleSolve(bot, typeManager));

        return subcommands;
    }

}
