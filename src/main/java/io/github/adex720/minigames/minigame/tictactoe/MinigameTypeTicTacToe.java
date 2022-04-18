package io.github.adex720.minigames.minigame.tictactoe;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandTicTacToeSet;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.Set;

public class MinigameTypeTicTacToe extends DuelMinigameType<MinigameTicTacToe> {

    public MinigameTypeTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "tic-tac-toe", true, new TicTacToeAI(bot));
    }

    @Override
    public MinigameTicTacToe create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameTicTacToe.start(event, ci);
    }

    @Override
    public MinigameTicTacToe create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameTicTacToe.start(event, ci);
    }

    @Override
    public MinigameTicTacToe fromJson(JsonObject json) {
        return MinigameTicTacToe.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandTicTacToeSet(bot, typeManager));
    }
}
