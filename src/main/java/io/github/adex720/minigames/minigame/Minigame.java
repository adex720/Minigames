package io.github.adex720.minigames.minigame;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigame;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Minigame is a game you play with the bot and get rewards for finishing one
 *
 * @author adex720
 */
public abstract class Minigame implements IdCompound, JsonSavable<Minigame> {

    protected final MinigamesBot bot;
    protected final MinigameType<? extends Minigame> type;

    public final long id;
    public final boolean isParty;

    protected long lastActive; // used to find inactive parties

    private boolean finished;

    public Minigame(MinigamesBot bot, MinigameType<? extends Minigame> type, long id, boolean isParty, long lastActive) {
        this.bot = bot;
        this.type = type;
        this.id = id;
        this.isParty = isParty;
        this.lastActive = lastActive;

        if (requiresLockedParty() && isParty) {
            bot.getPartyManager().getParty(id).lock();
        }

        finished = false;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public MinigameType<? extends Minigame> getType() {
        return type;
    }

    public String finish(Replyable replyable, CommandInfo commandInfo, boolean won) {
        finished = true;

        bot.getMinigameManager().deleteMinigame(id);
        Profile profile = commandInfo.profile();

        if (!profile.isInParty()) {
            return finishForUser(replyable, profile, won, true);

        }
        if (isEveryoneOnSameTeam()) {
            return finishForParty(replyable, commandInfo.party(), won);
        }

        Long[] winnerIds;

        if (this instanceof PartyCompetitiveMinigame minigame) {
            winnerIds = minigame.getWinners();
        } else {
            // This should never be reached, but it's still included to prevent possible errors if something is wrong.
            winnerIds = new Long[]{commandInfo.authorId()};
        }

        // Players who lost
        for (long user : commandInfo.party().getMembersWithOwner()) {
            if (!Util.containsEqual(winnerIds, user)) {
                finishForUser(replyable, bot.getProfileManager().getProfile(user), false, false);
            }
        }

        // PLayers who won
        // Since this is never supposed to use the user from CommandInfo, it's not worth checking it and getting profile from there.
        String reply = "";
        for (long userId : winnerIds) {
            if (reply.isEmpty()) {
                reply = finishForUser(replyable, bot.getProfileManager().getProfile(userId), won, true);
                continue;
            }

            finishForUser(replyable, bot.getProfileManager().getProfile(userId), won, false);
        }
        return reply;
    }

    public String finishForUser(Replyable replyable, Profile profile, boolean won, boolean shouldReply) {
        String rewards = addRewards(replyable, profile, won);

        if (shouldReply) {
            if (replyable.isWebhookBased()) {
                replyable.reply(rewards, Button.primary(getReplayButtonId(), "Play again")); // Add replay button
            } else {
                replyable.reply(rewards);
            }
        }

        appendQuest(replyable, profile, won); // Update quests and stats
        appendStats(profile, won);

        return rewards;
    }

    public String getReplayButtonId() {
        return "replay-" + type.name + "-" + id;
    }

    public String finishForParty(Replyable replyable, Party party, boolean won) {
        for (long userId : party.getMembersWithOwner()) {
            finishForUser(replyable, bot.getProfileManager().getProfile(userId), won, false);
        }

        return "You received rewards for the ended minigame";
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
            coins = getReward(random); // 100-250 coins for won minigame

            if (random.nextInt(3) == 0) { // If won there's a 33% chance to get a crate.
                CrateType reward = coins < 200 ? CrateType.COMMON : CrateType.UNCOMMON;
                profile.addCrate(reward);
                return "You received " + reward.getNameWithArticle() + " crate!";
            }

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

    public void delete(@Nullable Replyable replyable) {
        bot.getMinigameManager().deleteMinigame(id);

        if (requiresLockedParty() && isParty) {
            bot.getPartyManager().getParty(id).clearLock(replyable);
        }

        finished = true;
    }

    /**
     * Deletes the minigame.
     *
     * @param replyable Replyable to use to inform about party status chances. Can be null
     * @return message to send, defaults at an empty String.
     */
    public String quit(@Nullable Replyable replyable) {
        delete(replyable);
        return "";
    }

    /**
     * Updates the stored value when the minigame was played.
     * Calling this on each interaction is important so the minigame doesn't get deleted as inactive.
     *
     * @param commandInfo commandInfo to calculate party with.
     *                    If the commandInfo is not created or easily accessible,
     *                    just put null, and it'll be calculated if needed
     */
    public void active(@Nullable CommandInfo commandInfo) {
        lastActive = System.currentTimeMillis();

        if (isParty) {
            Party party;
            if (commandInfo != null) party = commandInfo.party();
            else party = bot.getPartyManager().getParty(id);

            party.active();
        }
    }

    public boolean isInactive(long limit) {
        return lastActive <= limit;
    }

    protected boolean isEveryoneOnSameTeam() {
        return true;
    }

    /**
     * Returns the state this minigame is in.
     * Most minigames don't have a state so default value of 1 is used.
     */
    public int getState() {
        return 1;
    }

    /**
     * Sets the state of the minigame.
     * This should only be used at the start of the minigame.
     */
    public void setState(String mode) {
    }

    /**
     * A locked party can chance its size.
     * If a member leaves or gets kicked the current minigame is removed.
     *
     * @return does the minigame require locked party.
     */
    public boolean requiresLockedParty() {
        return false;
    }

    /**
     * Returns a number between 100 and 250.
     * The better the minigame was played the more coins should be given.
     * The random should be used almost always and with relatively large interval for amount of coins.
     * There is also a ⅓ chance for the reward to be a crate.
     * If the reward is less than 200 it's a common crate and if it's 200 or higher it's an uncommon crate.
     * This method is not called if the minigame was lost.
     *
     * @param random random to use.
     * @return amount of coins to give as reward.
     */
    public abstract int getReward(Random random);

    public boolean shouldStart() {
        return !finished;
    }

}
