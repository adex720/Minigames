package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author adex720
 */
public class CommandGuildBuy extends Subcommand {

    public CommandGuildBuy(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "buy", "Purchases an upgrade from the guild shop.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOptions(new OptionData(OptionType.INTEGER, "perk", "Perk to upgrade", true).addChoices(getChoices()))
                .addOption(OptionType.INTEGER, "count", "Times to upgrade.", false);
    }

    private Command.Choice[] getChoices() {
        return new Command.Choice[]{new Command.Choice("Name", 0),
                new Command.Choice("Foo", 1),
                new Command.Choice("Bah", 2)};
    }
}
