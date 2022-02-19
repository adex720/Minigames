package io.github.adex720.minigames.minigame.duel;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.concurrent.ThreadLocalRandom;

public abstract class DuelMinigame extends Minigame {

    public static final int FIRST_PLAYER_WON = 1;
    public static final int SECOND_PLAYER_WON = 2;
    public static final int DRAW = 3;

    protected final long opponentId;

    protected boolean isFirstPlayersTurn;

    public DuelMinigame(MinigamesBot bot, MinigameType<? extends Minigame> type, long id, long opponentId, boolean isParty, long lastActive) {
        super(bot, type, id, isParty, lastActive);
        this.opponentId = opponentId;

        isFirstPlayersTurn = ThreadLocalRandom.current().nextBoolean();
    }

    public boolean checkForAIMove() {
        if (isParty) return false;

        makeAIMove();
        isFirstPlayersTurn = true;
        return true;
    }

    public void makeAIMove(){
        if (type instanceof DuelMinigameType<? extends DuelMinigame> duelMinigameType){
            assert duelMinigameType.ai != null;
            duelMinigameType.ai.makeMove(this);
        }
    }

    protected abstract EmbedBuilder getEmbedBase();

    public MessageEmbed getEmbedWithField(MessageEmbed.Field field) {
        return getEmbedBase().addField(field).build();
    }

    public MessageEmbed getEmbedWithField(String name, String value) {
        return getEmbedBase().addField(new MessageEmbed.Field(name, value, true)).build();
    }

    public MessageEmbed getEmbedWithFields(String name1, String value1, String name2, String value2) {
        return getEmbedBase().addField(new MessageEmbed.Field(name1, value1, true))
                .addField(name2, value2, true).build();
    }

    public MessageEmbed getEmbed() {
        return getEmbedBase().build();
    }

    public void finish(SlashCommandEvent event, CommandInfo commandInfo, int winState) {
        super.finish(event, commandInfo, winState == FIRST_PLAYER_WON);

        appendQuest(opponentId, winState == SECOND_PLAYER_WON);
    }
}
