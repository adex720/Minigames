package io.github.adex720.minigames.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.command.Command;
import io.github.adex720.minigames.command.CommandInfo;
import io.github.adex720.minigames.manager.command.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    private final MinigamesBot bot;

    private final CommandManager commandManager;

    public CommandListener(MinigamesBot bot, CommandManager commandManager) {
        this.bot = bot;
        this.commandManager = commandManager;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.isFromGuild()) event.reply("Commands cannot be used on dms").queue();

        String commandName = event.getName();
        Member member = event.getInteraction().getMember();
        long userId = member.getIdLong();
        //TODO: check if user is banned

        for (Command command : commandManager.COMMANDS) {
            if (commandName.equals(command.name)) {
                //event.deferReply().queue();
                CommandInfo commandInfo = new CommandInfo(() -> bot.getProfileManager().getProfile(userId).isInParty(),
                        () -> bot.getPartyManager().getParty(bot.getProfileManager().getProfile(userId).getPartyId()),
                        () -> bot.getProfileManager().hasProfile(userId),
                        () -> bot.getProfileManager().getProfile(userId),
                        member::getUser);
                command.onRun(event, commandInfo);
            }
        }

    }
}
