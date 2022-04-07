package io.github.adex720.minigames.discord.command.user.booster;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandUse extends Command {

    public CommandUse(MinigamesBot bot) {
        super(bot, "use", "Uses a booster.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        int id = (int) event.getOption("type").getAsLong();

        event.getHook().sendMessage(ci.profile().useBooster(id)).queue();
        return true;
    }

    @Override
    protected CommandData createCommandData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, "type", "Type of booster", true);

        for (int id = 0; id < BoosterRarity.RARITIES_AMOUNT; id++) {
            optionData.addChoice(BoosterRarity.get(id).name, id);
        }

        return super.createCommandData()
                .addOptions(optionData);
    }
}
