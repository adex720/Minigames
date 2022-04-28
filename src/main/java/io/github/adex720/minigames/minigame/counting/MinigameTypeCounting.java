package io.github.adex720.minigames.minigame.counting;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandCountingCount;
import io.github.adex720.minigames.discord.command.minigame.CommandCountingLastCounter;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyTeamMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeCounting extends PartyTeamMinigameType<MinigameCounting> {

    private final HashMap<Long, Integer> REPLAY_MODES; // Stores counting mode for replay buttons

    public MinigameTypeCounting(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "counting", 3);
        REPLAY_MODES = new HashMap<>();
    }

    @Override
    public MinigameCounting create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameCounting.start(event, ci);
    }

    @Override
    public MinigameCounting create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameCounting.start(event, ci, getState(ci.gameId()));
    }

    @Override
    public void createPlayCommand() {
        bot.getCommandManager().parentCommandPlay.createSubcommand(this,
                new OptionData(OptionType.INTEGER, "mode", "Type of counting", false)
                        .addChoice("base 10", MinigameCounting.MODE_BASE_10_ID)
                        .addChoice("hexadecimal", MinigameCounting.MODE_HEXADECIMAL_ID)
                        .addChoice("binary", MinigameCounting.MODE_BINARY_ID)
                        .addChoice("alphabets", MinigameCounting.MODE_LETTERS_ID));
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
        return Set.of(
                new CommandCountingCount(bot, typeManager),
                new CommandCountingLastCounter(bot, typeManager));
    }

    @Override
    public int getState(long gameId) {
        return REPLAY_MODES.getOrDefault(gameId, getDefaultState());
    }

    @Override
    public int getDefaultState() {
        return MinigameCounting.MODE_BASE_10_ID;
    }
}
