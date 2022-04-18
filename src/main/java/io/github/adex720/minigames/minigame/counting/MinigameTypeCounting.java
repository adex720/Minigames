package io.github.adex720.minigames.minigame.counting;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Set;

public class MinigameTypeCounting extends PartyMinigameType<MinigameCounting> {

    public MinigameTypeCounting(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "counting", 2);
    }

    @Override
    public MinigameCounting create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameCounting.start(event, ci);
    }

    @Override
    public MinigameCounting create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameCounting.start(event, ci);
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
        return Set.of();
    }
}
