package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandOpen extends Command {

    public CommandOpen(MinigamesBot bot) {
        super(bot, "open", "Opens crates.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return false;
    }

    @Override
    protected CommandData createCommandData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, "type", "Type of crate", false);

        for (int id = 0; id < CrateType.TYPES_AMOUNT; id++) {
            optionData.addChoice(CrateType.get(id).name, id);
        }

        return super.createCommandData()
                .addOptions(optionData)
                .addOption(OptionType.STRING, "amount", "Amount of crates to open", false);
    }

}
