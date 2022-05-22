package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

/**
 * A command that can be navigated with 'previous'- and 'next'-buttons.
 *
 * @author adex720
 */
public abstract class PageCommand extends Command {

    protected PageCommand(MinigamesBot bot, String name, String description, CommandCategory category) {
        super(bot, name, description, category);

        bot.getPageMovementManager().registerPageCommand(this);
    }

    public abstract void onPageMove(ButtonInteractionEvent event, CommandInfo ci, String[] args, int page);

    public Button getButtonForPage(int page, String label, boolean disabled, String... args) {
        return new ButtonImpl("page-" + name + "-" + page + appendArgs(args), label, ButtonStyle.SECONDARY, disabled, null);
    }

    public String appendArgs(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append('-').append(arg);
        }

        return stringBuilder.toString();
    }

}
