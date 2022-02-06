package io.github.adex720.minigames.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public abstract class Command {

    public static final Color SUCCESSFUL = new Color(0, 186, 0);

    protected final MinigamesBot bot;

    public final String name;
    public final String description;

    public final CommandCategory Category;

    public boolean requiresProfile;

    protected Command(MinigamesBot bot, String name, String description, CommandCategory category) {
        this.bot = bot;
        this.name = name.replace(' ', '-');
        this.description = description;
        Category = category;

        requiresProfile = false;
    }

    public boolean onRun(SlashCommandEvent event, CommandInfo ci) {
        if (requiresProfile) {
            if (!ci.hasProfile()) {
                event.reply(ci.authorMention() + "You need to create a profile with the start-command to use this command!").queue();
                return true;
            }
        }
        return execute(event, ci);
    }

    public abstract boolean execute(SlashCommandEvent event, CommandInfo ci);


    public CommandData createCommandData() {
        return new CommandData(name, description);
    }

    protected void requiresProfile() {
        requiresProfile = true;
    }

}
