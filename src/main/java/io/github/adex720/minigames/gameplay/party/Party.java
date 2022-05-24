package io.github.adex720.minigames.gameplay.party;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Party is a group of users who can play minigames together.
 * If a party finished a minigame everyone in it receives rewards.
 *
 * @author adex720
 */
public class Party implements JsonSavable<Party>, IdCompound {

    public static final int MAX_SIZE = 10;

    private final MinigamesBot bot;

    private long owner;
    private final Set<Long> members;
    private final Set<Long> activeMembers; // members who have participated on current minigame

    private final Set<Long> invites;
    private boolean isPublic;

    private boolean isLocked;

    private long lastActive;

    public Party(MinigamesBot bot, long ownerId, long... memberIds) {
        this.bot = bot;
        this.owner = ownerId;
        this.members = new HashSet<>();
        activeMembers = new HashSet<>();

        invites = new HashSet<>();

        for (long id : memberIds) {
            this.members.add(id);
        }

        isPublic = true;

        lastActive = System.currentTimeMillis();
    }

    public long getOwnerId() {
        return owner;
    }

    public int size() {
        return members.size() + 1;
    }

    public int sizeWithInvites() {
        return size() + invites.size();
    }

    public boolean isFull() {
        return size() >= MAX_SIZE;
    }

    public boolean isFullWithInvites() {
        return sizeWithInvites() >= MAX_SIZE;
    }

    public boolean removeMember(long memberId) {
        return members.remove(memberId);
    }

    public boolean addMember(long memberId) {
        if (size() < MAX_SIZE && !members.contains(memberId)) {
            members.add(memberId);
            return true;
        }

        return false;
    }

    public boolean transfer(long newOwner) {
        if (members.add(owner)) {
            members.remove(newOwner);
            owner = newOwner;
            return true;
        }

        return false;
    }

    public boolean isInParty(long memberId) {
        if (owner == memberId) return true;

        return members.contains(memberId);
    }

    public Set<Long> getMembersWithoutOwner() {
        return new HashSet<>(this.members);
    }

    public Set<Long> getMembersWithOwner() {
        Set<Long> members = new HashSet<>(this.members);
        members.add(owner);

        return members;
    }

    public Set<Long> getActiveMembers() {
        return activeMembers;
    }

    /**
     * @return the first element of {@link Set#toArray()} is called from the list of non owner members.
     */
    public long getMemberId() {
        return (long) members.toArray()[0];
    }

    /**
     * Updates the party timestamp used for clearing inactive parties.
     */
    public void active() {
        lastActive = System.currentTimeMillis();
    }

    /**
     * Updates the party timestamp used for clearing inactive parties.
     *
     * @param userId id of the user who is active on the party.
     */
    public void active(long userId) {
        lastActive = System.currentTimeMillis();
        activeMembers.add(userId);
    }

    public boolean isInactive(long limit) {
        return lastActive <= limit;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void makePublic() {
        if (isLocked) return;
        isPublic = true;
    }

    public void makePrivate() {
        if (isLocked) return;
        isPublic = false;
    }

    /**
     * Locked party can't receive new members even from invites.
     * The locked status breaks if someone leaves the party.
     */

    public boolean isLocked() {
        return isLocked;
    }

    public void lock() {
        isLocked = true;
        clearInvites();
    }

    public void clearLock(@Nullable Replyable replyable) {
        if (!isLocked) return;

        isLocked = false;
        Minigame minigame = bot.getMinigameManager().getMinigame(getId());

        if (minigame == null) return;
        if (!minigame.requiresLockedParty()) return;

        minigame.delete(replyable);
        if (replyable == null) return;
        replyable.reply("Your party no longer meets the requirements of the current minigame. The minigame was deleted.");
    }

    public void invite(long id) {
        active();
        if (isLocked) return;

        invites.add(id);
        Util.schedule(() -> invites.remove(id), 60000);
    }

    public boolean isInvited(long id) {
        return invites.contains(id);
    }

    public void clearInvites() {
        invites.clear();
    }

    public void updatePartyId() {
        members.forEach(id -> bot.getProfileManager().getProfile(id).partyJoined(owner));
        bot.getProfileManager().getProfile(owner).partyJoined(owner);
    }

    /**
     * @return The embed message on /party info
     */
    public MessageEmbed getInfo(User user) {
        active();
        StringBuilder stringBuilder = new StringBuilder()
                .append("**Size: ")
                .append(size())
                .append("**");

        if (size() >= MAX_SIZE) {
            stringBuilder.append("\n**The party is full.");
        }

        if (isLocked) {
            stringBuilder.append("\nThe party is **locked**.");
        } else if (isPublic) {
            stringBuilder.append("\nThe party is **public**.");
        } else {
            stringBuilder.append("\nThe party is **private**.");
        }

        Minigame minigame = bot.getMinigameManager().getMinigame(getId());

        if (minigame == null) {
            stringBuilder.append("\nThis party isn't currently playing a minigame.");
        } else {
            stringBuilder.append("\nThis party is currently playing ").append(minigame.getType().getNameWithSpaces()).append('.');
        }

        return new EmbedBuilder()
                .setTitle("PARTY INFO")
                .addField("Owner: " + user.getAsTag(), stringBuilder.toString(), false)
                .setColor(Util.getColor(owner))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    /**
     * @return The embed message on /party members
     */
    public MessageEmbed getMembers(User user) {
        active();
        StringBuilder stringBuilder = new StringBuilder();

        if (!members.isEmpty()) {
            stringBuilder.append("**Members:**");
            for (long memberId : members) {
                stringBuilder.append(" - <@!")
                        .append(memberId)
                        .append(">");
            }
            if (size() >= MAX_SIZE) {
                stringBuilder.append("\n**The party is full.");
            }
        }

        return new EmbedBuilder()
                .setTitle("PARTY MEMBERS")
                .addField("Owner: " + user.getAsTag(), stringBuilder.toString(), false)
                .setColor(Util.getColor(owner))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();

    }

    @Override
    public int hashCode() {
        return Integer.hashCode((int) owner);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Party party) {
            return party.owner == owner;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Party owner: " + owner + ", members: " + size();
    }

    @Override
    public Long getId() {
        return getOwnerId();
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", owner);

        JsonArray members = new JsonArray();
        for (long memberId : this.members) {
            members.add(memberId);
        }
        json.add("members", members);

        return json;
    }

    public static Party fromJson(MinigamesBot bot, JsonObject json) {
        long ownerId = JsonHelper.getLong(json, "id");

        JsonArray membersJson = JsonHelper.getJsonArray(json, "members");

        int members = membersJson.size();
        long[] memberIds = new long[members];
        for (int i = 0; i < members; i++) {
            memberIds[i] = membersJson.get(i).getAsLong();
        }

        return new Party(bot, ownerId, memberIds);
    }

    public void onCreate() {
    }

    public void onDelete() {
        bot.getMinigameManager().deleteMinigame(getId());

        bot.getProfileManager().getProfile(owner).partyLeft();
        members.forEach(id -> bot.getProfileManager().getProfile(id).partyLeft());
    }

    public void onTransfer(long oldOwner) {
        updatePartyId();
        active();
    }

    public void onMemberJoin(long member) {
        active();
    }

    public void onMemberLeave(long member) {
        activeMembers.remove(member);
    }

    public void onMemberKicked(long member) {
        activeMembers.remove(member);
    }

    public void onMinigameStart(){
        activeMembers.clear();
    }

    public void onMinigameEnd(){
        activeMembers.clear();
    }

}
