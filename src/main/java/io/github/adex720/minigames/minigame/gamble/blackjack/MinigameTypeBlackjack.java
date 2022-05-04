package io.github.adex720.minigames.minigame.gamble.blackjack;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandBlackjackDouble;
import io.github.adex720.minigames.discord.command.minigame.CommandBlackjackHit;
import io.github.adex720.minigames.discord.command.minigame.CommandBlackjackStand;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.gamble.GambleMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeBlackjack extends GambleMinigameType<MinigameBlackjack> {

    public MinigameTypeBlackjack(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "blackjack", 1f);
    }

    @Override
    public MinigameBlackjack create(SlashCommandEvent event, CommandInfo ci) {
        int bet = (int) event.getOption("bet").getAsLong();

        return MinigameBlackjack.start(event, ci, bet);
    }

    @Override
    public MinigameBlackjack create(ButtonClickEvent event, CommandInfo ci) {
        int bet = Integer.parseInt(ci.args()[3]);
        // 0 = replay
        // 1 = blackjack
        // 2 = game id
        // 3 = bet

        return MinigameBlackjack.start(event, ci, bet);
    }

    @Override
    public void createPlayCommand() {
        bot.getCommandManager().parentCommandPlay.createSubcommand(this,
                new OptionData(OptionType.INTEGER, "bet", "Bet", true));
    }

    @Override
    public MinigameBlackjack fromJson(JsonObject json) {
        return MinigameBlackjack.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandBlackjackHit(bot, typeManager),
                new CommandBlackjackStand(bot, typeManager),
                new CommandBlackjackDouble(bot, typeManager)
        );
    }
}
