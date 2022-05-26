package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandGuildDelete extends Subcommand {

    public CommandGuildDelete(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "delete", "Deletes your guild.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOption(OptionType.BOOLEAN, "confirmation", "To make sure you don't accidentally delete the guild.", true);
    }
}
