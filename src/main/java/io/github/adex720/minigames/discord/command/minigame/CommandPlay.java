package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.ParentCommand;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPlay extends ParentCommand {

    public CommandPlay(MinigamesBot bot) {
        super(bot, "play", "Starts a minigame.", CommandCategory.MINIGAME);
        requiresProfile();
    }

    public void createSubcommand(MinigameType<? extends Minigame> type) {
        Subcommand subcommand = new SubcommandPlay(type);
        addSubcommand(subcommand);
    }

    public class SubcommandPlay extends Subcommand {

        private final MinigameType<? extends Minigame> minigame;

        protected SubcommandPlay(MinigameType<? extends Minigame> minigame) {
            super(CommandPlay.this.bot, CommandPlay.this, minigame.name, minigame.description, CommandCategory.MINIGAME);
            this.minigame = minigame;
        }

        @Override
        public boolean execute(SlashCommandEvent event, CommandInfo ci) {
            if (ci.hasMinigame()){
               event.getHook().sendMessage(ci.minigame().quit()).queue();
            }

            Minigame minigame = this.minigame.create(event, ci);
            bot.getMinigameManager().addMinigame(minigame);

            return true;
        }
    }

}
