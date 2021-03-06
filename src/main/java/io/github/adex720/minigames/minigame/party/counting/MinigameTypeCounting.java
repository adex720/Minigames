package io.github.adex720.minigames.minigame.party.counting;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandCountingCount;
import io.github.adex720.minigames.discord.command.minigame.CommandCountingLastCounter;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.party.PartyTeamMinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeCounting extends PartyTeamMinigameType<MinigameCounting> {

    public MinigameTypeCounting(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "counting", 3);
    }

    @Override
    public @Nullable MinigameCounting create(Replyable replyable, CommandInfo ci) {
        return null;
    }

    @Override
    public @Nullable MinigameCounting create(Replyable replyable, CommandInfo ci, SlashCommandInteractionEvent event) {
        return null;
    }

    @Override
    public @Nullable MinigameCounting create(Replyable replyable, CommandInfo ci, String[] buttonArgs) {
        int type;
        if (buttonArgs.length <= 3) type = getDefaultState();
        else type = Integer.parseInt(ci.args()[3]);
        // Button id args
        // 0 = "replay"
        // 1 = "counting"
        // 2 = game id
        // 3 = type (optional)

        return MinigameCounting.start(replyable, ci, type);
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

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandCountingCount(bot, typeManager),
                new CommandCountingLastCounter(bot, typeManager));
    }

    @Override
    public int getDefaultState() {
        return MinigameCounting.MODE_BASE_10_ID;
    }
}
