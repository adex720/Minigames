package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * @author adex720
 */
public class CommandQuit extends Command {

    public CommandQuit(MinigamesBot bot) {
        super(bot, "quit", "Quits your current minigame.", CommandCategory.MINIGAME);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        if (!ci.hasMinigame()) {
            event.getHook().sendMessage("You don't have an ongoing minigame").queue();
            return true;
        }

        Minigame minigame = ci.minigame();
        event.getHook().sendMessage(ci.minigame().quit(Replyable.from(event))) // Finish message
                .addActionRow(Button.primary(minigame.getReplayButtonId(), "Start again")) // Add button to replay
                .queue();

        return true;
    }
}
