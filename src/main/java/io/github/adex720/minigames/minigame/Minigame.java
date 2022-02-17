package io.github.adex720.minigames.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public abstract class Minigame implements IdCompound, JsonSavable<Minigame> {

    protected final MinigamesBot bot;
    protected final MinigameType<? extends Minigame> type;

    public final long id;
    public final boolean isParty;

    protected long lastActive;

    public Minigame(MinigamesBot bot, MinigameType<? extends Minigame> type, long id, boolean isParty, long lastActive) {
        this.bot = bot;
        this.type = type;
        this.id = id;
        this.isParty = isParty;
        this.lastActive = lastActive;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public MinigameType<? extends Minigame> getType() {
        return type;
    }

    public void finish(SlashCommandEvent event, boolean won) {
        bot.getMinigameManager().deleteMinigame(id);

        event.getHook().sendMessage("Press this button to play again")
                .addActionRow(Button.primary("play-again", "Play again")).queue();

        bot.getReplayManager().addReplay(id, type);

        appendQuest(id, won);
    }

    public void appendQuest(long id, boolean won) {
        // TODO: append quests
    }

    public void delete() {
        bot.getMinigameManager().deleteMinigame(id);
    }

    public String quit() {
        delete();
        return "";
    }

    public void active() {
        lastActive = System.currentTimeMillis();
    }

    public boolean isInactive(long limit) {
        return lastActive <= limit;
    }

}
