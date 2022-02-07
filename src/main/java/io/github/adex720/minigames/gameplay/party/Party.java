package io.github.adex720.minigames.gameplay.party;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class Party implements JsonSavable<Party>, IdCompound {

    public static final int MAX_SIZE = 10;

    private final MinigamesBot bot;

    private long owner;
    private final Set<Long> members;

    private final Set<Long> invites;
    private boolean isPublic;

    private long lastActive;

    public Party(MinigamesBot bot, long ownerId, long... memberIds) {
        this.bot = bot;
        this.owner = ownerId;
        this.members = new HashSet<>();

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

    public void active() {
        lastActive = System.currentTimeMillis();
    }

    public boolean isInactive(long limit) {
        return lastActive <= limit;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void makePublic() {
        isPublic = true;
    }

    public void makePrivate() {
        isPublic = false;
    }

    public void invite(long id) {
        invites.add(id);
        Util.schedule(() -> invites.remove(id), 60000);
    }

    public boolean isInvited(long id) {
        return invites.contains(id);
    }

    public void updatePartyId() {
        members.forEach(id -> bot.getProfileManager().getProfile(id).partyJoined(owner));
        bot.getProfileManager().getProfile(owner).partyJoined(owner);
    }

    public MessageEmbed getInfo(User user) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("**Size: ")
                .append(size())
                .append("**");

        if (size() >= MAX_SIZE) {
            stringBuilder.append("\n**The party is full.");
        }

        // TODO: show current minigame

        return new EmbedBuilder()
                .setTitle("PARTY INFO")
                .addField("Owner: " + user.getAsTag(), stringBuilder.toString(), false)
                .setColor(Util.getColor(owner))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    public MessageEmbed getMembers(User user) {

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
        return (int) owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Party party) {
            return party.owner == owner;
        } else return false;
    }

    @Override
    public String toString() {
        return super.toString();
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
        bot.getProfileManager().getProfile(owner).partyLeft();
        members.forEach(id -> bot.getProfileManager().getProfile(id).partyLeft());
    }

    public void onTransfer(long oldOwner) {
        updatePartyId();
    }

    public void onMemberJoin(long member) {

    }

    public void onMemberLeave(long member) {

    }

    public void onMemberKicked(long member) {

    }


}