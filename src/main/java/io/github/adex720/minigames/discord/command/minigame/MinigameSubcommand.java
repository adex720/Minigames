package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;

public abstract class MinigameSubcommand extends Subcommand {

    public MinigameSubcommand(MinigamesBot bot, MinigameType<? extends Minigame> type, String name, String description) {
        super(bot, type.getCommand(), name, description, CommandCategory.MINIGAME);
        requiresProfile();
    }

}
