package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public abstract class Command {

    public static final Color SUCCESSFUL = new Color(0, 186, 0);

    protected final MinigamesBot bot;
    private final CommandData commandData;

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
        this.commandData = createCommandData();
    }

    public boolean onRun(SlashCommandEvent event, CommandInfo ci) {
        if (requiresProfile) {
            if (!ci.hasProfile()) {
                event.getHook().sendMessage(ci.authorMention() + "You need to create a profile with the start-command to use this command!").queue();
                return true;
            }
        }
        return execute(event, ci);
    }

    public abstract boolean execute(SlashCommandEvent event, CommandInfo ci);


    protected CommandData createCommandData() {
        return new CommandData(name, description);
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public boolean isSubcommand() {
        return false;
    }

    protected void requiresProfile() {
        requiresProfile = true;
    }

    public String getMainName() {
        return name;
    }

    public String getWholeName() {
        return name;
    }
}
