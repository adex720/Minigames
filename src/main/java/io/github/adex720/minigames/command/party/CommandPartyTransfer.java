package io.github.adex720.minigames.command.party;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandCategory;
import io.github.adex720.minigames.command.CommandInfo;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandPartyTransfer  extends Command {

    public CommandPartyTransfer(MinigamesBot bot) {
        super(bot, "", "", CommandCategory.PARTY);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        return true;
    }
}
