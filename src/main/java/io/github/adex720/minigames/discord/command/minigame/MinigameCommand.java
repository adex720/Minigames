package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.ParentCommand;
import io.github.adex720.minigames.discord.command.Subcommand;

public class MinigameCommand extends ParentCommand {

    public MinigameCommand(MinigamesBot bot, String name, String description) {
        super(bot, name, description, CommandCategory.MINIGAME);
    }

    @Override
    public void addSubcommand(Subcommand subcommand) {
        super.addSubcommand(subcommand);
        bot.getCommandManager().addSubcommand(subcommand);
    }
}