package io.github.adex720.minigames.minigame.duel.connect4;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandConnect4Drop;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

public class MinigameTypeConnect4 extends DuelMinigameType<MinigameConnect4> {

    public MinigameTypeConnect4(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "connect4");
    }

    @Override
    public MinigameConnect4 create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameConnect4.start(event, ci);
    }

    @Override
    public MinigameConnect4 create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameConnect4.start(event, ci);
    }

    @Override
    public MinigameConnect4 fromJson(JsonObject json) {
        return MinigameConnect4.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(
                new CommandConnect4Drop(bot, typeManager)
        );
    }
}
