package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class CommandQuit extends Command {

    public CommandQuit(MinigamesBot bot) {
        super(bot, "quit", "Quits your current minigame.", CommandCategory.MINIGAME);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (!ci.hasMinigame()) {
            event.getHook().sendMessage("You don't have an ongoing minigame").queue();
            return true;
        }

        event.getHook().sendMessage(ci.minigame().quit(Replyable.from(event))) // Finish message
                .addActionRow(Button.primary("play-again", "Start again")) // Add button
                .queue();

        Minigame minigame = ci.minigame();
        minigame.addReplay(); // Save to replay data

        return true;
    }
}
