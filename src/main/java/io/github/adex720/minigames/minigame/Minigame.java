package io.github.adex720.minigames.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Random;

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

    public void finish(SlashCommandEvent event, CommandInfo commandInfo, boolean won) {
        bot.getMinigameManager().deleteMinigame(id);
        Profile profile = commandInfo.profile();

        String rewards = addRewards(profile, won);

        event.getHook().sendMessage(rewards)
                .addActionRow(Button.primary("play-again", "Play again")).queue();
        bot.getReplayManager().addReplay(id, type);

        appendQuest(profile, won);
        appendStats(profile, won);
    }

    public void appendQuest(Profile profile, boolean won) {
        if (won) {
            profile.appendQuests(q -> q.minigamePlayed(this.type, profile), q -> q.minigameWon(this.type, profile));
        } else {
            profile.appendQuests(q -> q.minigamePlayed(this.type, profile));
        }
    }

    public String addRewards(Profile profile, boolean won) {
        int coins = 50;
        if (won) {
            Random random = bot.getRandom();
            if (random.nextInt(3) == 0) {
                CrateType reward = random.nextBoolean() ? CrateType.COMMON : CrateType.UNCOMMON;
                profile.addCrate(reward);
                return "You received " + reward.getNameWithArticle() + " crate!";
            }

            coins = random.nextInt(100, 250);
        }

        profile.addCoins(coins, true);
        return "You received " + coins + " coins!";
    }

    public void appendStats(Profile profile, boolean won) {
        profile.increaseStat(type.getNameWithSpaces() + " games played");
        profile.increaseStat("minigames played");

        if (won) {
            profile.increaseStat(type.getNameWithSpaces() + " games won");
            profile.increaseStat("minigames won");
        }
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
