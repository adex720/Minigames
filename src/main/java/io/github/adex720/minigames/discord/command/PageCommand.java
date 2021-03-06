package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.command.PageMovementManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

/**
 * A command that can be navigated with 'previous'- and 'next'-buttons.
 * The button format is page-type-pageNumber-userId-optionalArguments.
 *
 * @author adex720
 * @see PageMovementManager
 */
public interface PageCommand {

    void onPageMove(ButtonInteractionEvent event, CommandInfo ci, int page, String[] args);

    String getPageName();

    default void registerPageId(MinigamesBot bot){
        bot.getPageMovementManager().registerPageCommand(this);
    }

    default Button getButtonForPage(long userId, int page, String label, boolean disabled, String... args) {
        return new ButtonImpl("page-" + getPageName() + "-" + page + "-" + userId + appendArgs(args), label, ButtonStyle.SECONDARY, disabled, null);
    }

    default String appendArgs(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append('-').append(arg);
        }

        return stringBuilder.toString();
    }

}
