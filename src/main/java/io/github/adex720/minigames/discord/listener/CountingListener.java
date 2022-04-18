package io.github.adex720.minigames.discord.listener;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.counting.MinigameCounting;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CountingListener extends ListenerAdapter {

    private final MinigamesBot bot;

    public CountingListener(MinigamesBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()) return;

        CommandInfo commandInfo = CommandInfo.create(event, bot);
        if (!commandInfo.hasProfile()) return; // ignore messages from users with no bot data

        Party party = commandInfo.party();
        if (party == null) return; // not in party

        Minigame minigame = bot.getMinigameManager().getMinigame(party.getId());
        if (minigame == null) return; // no active minigame

        if (minigame instanceof MinigameCounting countingMinigame) {
            countingMinigame.onMessageReceive(event);
        }
    }
}
