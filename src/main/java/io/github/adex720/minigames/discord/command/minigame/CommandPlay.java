package io.github.adex720.minigames.discord.command.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.ParentCommand;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandPlay extends ParentCommand {

    public CommandPlay(MinigamesBot bot) {
        super(bot, "play", "Starts a minigame.", CommandCategory.MINIGAME);
        requiresProfile();
    }

    public void createSubcommand(MinigameType<? extends Minigame> type) {
        Subcommand subcommand = new SubcommandPlay(type);
        addSubcommand(subcommand);
    }

    public void createSubcommand(MinigameType<? extends Minigame> type, OptionData... optionData) {
        Subcommand subcommand = new SubcommandPlay(type, optionData);
        addSubcommand(subcommand);
    }

    public class SubcommandPlay extends Subcommand {

        private final MinigameType<? extends Minigame> minigame;

        private final OptionData[] optionData;

        protected SubcommandPlay(MinigameType<? extends Minigame> minigame) {
            super(CommandPlay.this.bot, CommandPlay.this, minigame.name, "Starts a game of " + minigame.name + ".", CommandCategory.MINIGAME);
            this.minigame = minigame;

            this.optionData = new OptionData[0];
        }

        protected SubcommandPlay(MinigameType<? extends Minigame> minigame, OptionData[] optionData) {
            super(CommandPlay.this.bot, CommandPlay.this, minigame.name, "Starts a game of " + minigame.name + ".", CommandCategory.MINIGAME);
            this.minigame = minigame;

            this.optionData = optionData;
        }

        @Override
        protected SubcommandData getSubcommandData() {
            return super.getSubcommandData().addOptions(optionData);
        }

        @Override
        public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
            if (!this.minigame.canStart(ci)) {
                event.getHook().sendMessage(this.minigame.getReplyForInvalidStartState(ci)).queue();
                return true;
            }

            if (ci.hasMinigame()) {
                String quitMessage = ci.minigame().quit(Replyable.from(event));
                if (!quitMessage.isEmpty()) event.getHook().sendMessage(quitMessage).queue();
            }

            Replyable replyable = Replyable.from(event);
            Minigame minigame = this.minigame.create(replyable, ci);
            if (minigame != null) {
                if (minigame.shouldStart()) { // This is false on scenarios like start card sum being 21 on blackjack
                    bot.getMinigameManager().addMinigame(minigame);
                }
                return true;
            }

            event.getHook().sendMessage(this.minigame.getReplyForNullAfterConstructor(ci)).queue();
            return true;
        }
    }

}
