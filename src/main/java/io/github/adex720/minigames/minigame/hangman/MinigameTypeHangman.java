package io.github.adex720.minigames.minigame.hangman;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandHangmanGuess;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeHangman extends MinigameType<MinigameHangman> {

    public MinigameTypeHangman(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "hangman", false, 1);
    }

    @Override
    public MinigameHangman create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameHangman.start(event, ci);
    }

    @Override
    public MinigameHangman create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameHangman.start(event, ci);
    }

    @Override
    public MinigameHangman fromJson(JsonObject json) {
        return MinigameHangman.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandHangmanGuess(bot, typeManager));
    }

    @Override
    public String getReplyForInvalidStartState() {
        return "";
    }

}
