package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Subcommand is a command which is associated to its {@link ParentCommand}.
 * If the command shown at Discord is "/foo bah", foo is the parent command and bah is a subcommand
 * If a command has any subcommands the parent command is unrunnable from Discord.
 *
 * @author adex720
 */
public abstract class Subcommand extends Command {

    protected final ParentCommand parent;

    public Subcommand(MinigamesBot bot, ParentCommand parent, String name, String description, CommandCategory category) {
        super(bot, name, description, category);
        this.parent = parent;
    }

    @Override
    public abstract boolean execute(SlashCommandInteractionEvent event, CommandInfo ci);

    @Override
    public String getFullName() {
        return parent.getFullName() + " " + getMainName();
    }

    protected SubcommandData getSubcommandData() {
        return new SubcommandData(name, description);
    }

    public void registerSubcommand() {
        parent.addSubcommand(this);
    }

}
