package io.github.adex720.minigames.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.Replyable;
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

    public void finish(Replyable replyable, CommandInfo commandInfo, boolean won) {
        bot.getMinigameManager().deleteMinigame(id);
        Profile profile = commandInfo.profile();

        if (isEveryoneOnSameTeam() && profile.isInParty()) {
            for (long userId : commandInfo.party().getMembersWithOwner()) {
                finishForUser(replyable, bot.getProfileManager().getProfile(userId), won);
            }
        } else {
            finishForUser(replyable, profile, won);
        }
    }

    public void finishForUser(Replyable replyable, Profile profile, boolean won) {
        String rewards = addRewards(replyable, profile, won);

        if (replyable.isWebhookBased()) {
            replyable.getWebhookMessageAction(rewards)
                    .addActionRow(Button.primary("play-again", "Play again")).queue(); // Add replay button
            bot.getReplayManager().addReplay(id, type);
        }

        appendQuest(replyable, profile, won); // Update quests and stats
        appendStats(profile, won);
    }

    public void appendQuest(Replyable replyable, Profile profile, boolean won) {
        if (won) {
            profile.appendQuests(q -> q.minigamePlayed(replyable, this.type, profile), q -> q.minigameWon(replyable, this.type, profile));
        } else {
            profile.appendQuests(q -> q.minigamePlayed(replyable, this.type, profile));
        }
    }

    public String addRewards(Replyable replyable, Profile profile, boolean won) {
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

        profile.addCoins(coins, true, replyable);
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
     *
     * @return message to send, defaults at an empty String.
     */
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

    protected boolean isEveryoneOnSameTeam() {
        return true;
    }

}
