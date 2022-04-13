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

    protected long lastActive; // used to find inactive parties

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

        String rewards = addRewards(event, profile, won);

        event.getHook().sendMessage(rewards)
                .addActionRow(Button.primary("play-again", "Play again")).queue(); // Add replay button
        bot.getReplayManager().addReplay(id, type);

        appendQuest(event, profile, won); // Update quests and stats
        appendStats(profile, won);
    }

    public void appendQuest(SlashCommandEvent event, Profile profile, boolean won) {
        if (won) {
            profile.appendQuests(q -> q.minigamePlayed(event, this.type, profile), q -> q.minigameWon(event, this.type, profile));
        } else {
            profile.appendQuests(q -> q.minigamePlayed(event, this.type, profile));
        }
    }

    public String addRewards(SlashCommandEvent event, Profile profile, boolean won) {
        int coins = 50; // 50 coins for lost minigame
        if (won) {
            Random random = bot.getRandom();
            if (random.nextInt(3) == 0) { // If won there's a 33% chance to get a crate.
                CrateType reward = random.nextBoolean() ? CrateType.COMMON : CrateType.UNCOMMON;
                profile.addCrate(reward);
                return "You received " + reward.getNameWithArticle() + " crate!";
            }

            coins = random.nextInt(100, 250); // 100-250 coins for won minigame
        }

        profile.addCoins(coins, true, event);
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

    /**
     * Deletes the minigame.
     * @return message to send, defaults at an empty String.
     * */
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
