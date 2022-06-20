package io.github.adex720.minigames.gameplay.manager.button;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.Manager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * A manager which manages actions on other commands or minigames based on button presses.
 *
 * @author adex720
 */
public abstract class ButtonManager extends Manager {

    public final String buttonName;
    public final boolean requiresProfile;

    protected ButtonManager(MinigamesBot bot, String name, boolean requiresProfile) {
        super(bot, name + "-button-manager");
        this.buttonName = name;
        this.requiresProfile = requiresProfile;

        bot.getButtonListener().addButtonManager(this);
    }

    public abstract void onButtonPressed(ButtonInteractionEvent event, CommandInfo commandInfo, String[] args);
}
