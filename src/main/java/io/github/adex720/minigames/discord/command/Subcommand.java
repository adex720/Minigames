package io.github.adex720.minigames.discord.command;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class Subcommand extends Command {

    protected final ParentCommand parent;

    public Subcommand(MinigamesBot bot, ParentCommand parent, String name, String description, CommandCategory category) {
        super(bot, name, description, category);
        this.parent = parent;
    }

    @Override
    public abstract boolean execute(SlashCommandEvent event, CommandInfo ci);

    @Override
    public String getWholeName() {
        return parent.getWholeName() + " " + getMainName();
    }

    protected SubcommandData getSubcommandData() {
        return new SubcommandData(name, description);
    }

    public void registerSubcommand() {
        parent.addSubcommand(this);
    }

}
