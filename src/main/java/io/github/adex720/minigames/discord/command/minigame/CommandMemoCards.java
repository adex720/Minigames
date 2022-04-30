package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.party.memo.MinigameMemo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.io.IOException;

/**
 * @author adex720
 */
public class CommandMemoCards extends MinigameSubcommand {

    public CommandMemoCards(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager.MEMO, "cards", "Shows the current card layout.");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        Minigame minigame = ci.minigame();

        if (minigame != null) {
            if (minigame.getType() == bot.getMinigameTypeManager().MEMO) {
                MinigameMemo memo = (MinigameMemo) minigame;
                try {
                    memo.sendImage(event, "Cards:");
                } catch (IOException e) {
                    bot.getLogger().error(e.getMessage());
                    return false;
                }

                return true;
            }
        }
        event.getHook().sendMessage("You don't have an ongoing memo game!").queue();
        return true;
    }

}
