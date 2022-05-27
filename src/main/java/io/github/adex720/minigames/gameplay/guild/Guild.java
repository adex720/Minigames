package io.github.adex720.minigames.gameplay.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author adex720
 */
public class Guild implements JsonSavable<Guild>, IdCompound { //TODO: record member join date

    public static final int MINIGAMES_WON_STAT_ID = 2;

    public static final int MAX_NAME_LENGTH = 15;
    public static final char[] INVALID_NAME_CHARACTERS = {'@', '\\'};

    public static final int MAX_SIZE = 10;

    private long ownerId;
    private String ownerTag;
    private final Set<Pair<Long, String>> members;
    private String name;

    private final Set<Long> elders;
    private final Set<Long> invites;
    private boolean isPublic;

    private final long createdTime;

    private int minigamesWonTotal;
    private int minigamesWonCurrentWeek;

    public Guild(long ownerId, String ownerTag, String name) {
        createdTime = System.currentTimeMillis();

        this.ownerId = ownerId;
        this.ownerTag = ownerTag;
        members = new HashSet<>();
        this.name = name;

        this.elders = new HashSet<>();
        this.invites = new HashSet<>();

        isPublic = true;

        minigamesWonTotal = 0;
        minigamesWonCurrentWeek = 0;
    }

    public Guild(long ownerId, String ownerTag, ArrayList<Pair<Long, String>> members, ArrayList<Long> elders, String name, long created, boolean isPublic, int minigamesWonTotal, int minigamesWonCurrentWeek) {
        this.ownerId = ownerId;
        this.ownerTag = ownerTag;

        this.members = new HashSet<>();
        this.members.addAll(members);
        this.name = name;

        this.elders = new HashSet<>();
        this.elders.addAll(elders);
        this.invites = new HashSet<>();

        this.isPublic = isPublic;

        this.createdTime = created;

        this.minigamesWonTotal = minigamesWonTotal;
        this.minigamesWonCurrentWeek = minigamesWonCurrentWeek;
    }

    public static boolean isNameValid(String name) {
        int length = name.length();
        if (length > MAX_NAME_LENGTH) return false;

        for (int i = 0; i < length; i++) {
            char checking = name.charAt(i);
            for (char illegalChar : INVALID_NAME_CHARACTERS) {
                if (checking == illegalChar) return false;
            }

            if (checking > 255) return false; // Non ascii characters are invalid
            if (checking < 0x20) return false; // Control characters are invalid
        }
        return true;
    }

    @Override
    public Long getId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public static Guild fromJson(JsonObject json) {
        long ownerId = JsonHelper.getLong(json, "owner");
        String ownerTag = JsonHelper.getString(json, "owner-tag");

        JsonArray membersJson = JsonHelper.getJsonArrayOrEmpty(json, "members");
        ArrayList<Pair<Long, String>> members = new ArrayList<>();
        ArrayList<Long> elders = new ArrayList<>();

        for (JsonElement jsonElement : membersJson) {
            JsonObject memberJson = jsonElement.getAsJsonObject();

            long userId = JsonHelper.getLong(memberJson, "id");
            String tag = JsonHelper.getString(memberJson, "tag");
            members.add(new Pair<>(userId, tag));

            if (memberJson.has("elder")) elders.add(userId);
        }

        String name = JsonHelper.getString(json, "name");

        boolean isPublic = JsonHelper.getBoolean(json, "public");
        long created = JsonHelper.getLong(json, "created");

        int minigamesWonTotal = JsonHelper.getInt(json, "wins");
        int minigamesWonCurrentWeek = JsonHelper.getInt(json, "wins-week");

        return new Guild(ownerId, ownerTag, members, elders, name, created, isPublic, minigamesWonTotal, minigamesWonCurrentWeek);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("owner", ownerId);
        json.addProperty("owner-tag", ownerTag);
        if (!members.isEmpty()) json.add("members", getMembersJson());

        json.addProperty("name", name);
        json.addProperty("public", isPublic);
        json.addProperty("created", createdTime);

        json.addProperty("wins", minigamesWonTotal);
        json.addProperty("wins-week", minigamesWonCurrentWeek);

        return json;
    }

    private JsonArray getMembersJson() {
        JsonArray json = new JsonArray();

        for (Pair<Long, String> member : members) {
            JsonObject memberJson = new JsonObject();

            memberJson.addProperty("id", member.first);
            memberJson.addProperty("tag", member.second);

            if (elders.contains(member.first)) memberJson.addProperty("elder", 1);

            json.add(memberJson);
        }

        return json;
    }

    /**
     * Returns true if the user is in the guild.
     *
     * @param userId Id of the user
     */
    public boolean isInGuild(long userId) {
        if (userId == ownerId) return true;

        for (Pair<Long, String> member : members) {
            if (member.first == userId) return true;
        }
        return false;
    }

    /**
     * Returns true if the user is in the guild but not the owner.
     *
     * @param userId Id of the user
     */
    public boolean isGuildMember(long userId) {
        for (Pair<Long, String> member : members) {
            if (member.first == userId) return true;
        }
        return false;
    }

    /**
     * Returns the amount of users on the guild.
     */
    public int size() {
        return members.size() + 1;
    }

    /**
     * Returns the amount of users on the guild excluding the owner.
     */
    public int sizeWithoutOwner() {
        return members.size();
    }

    /**
     * Returns the amount of users on the party or on the invite list.
     */
    public int sizeWithInvites() {
        return size() + invites.size();
    }

    /**
     * Returns true if the party is full.
     */
    public boolean isFull() {
        return size() >= MAX_SIZE;
    }

    /**
     * Returns true if the party is full after every invited user joins.
     */
    public boolean isFullWithInvites() {
        return sizeWithInvites() >= MAX_SIZE;
    }

    /**
     * Returns a {@link MessageEmbed} containing information about the guild.
     */
    public MessageEmbed getInfoMessage(User user) {
        return new EmbedBuilder()
                .setTitle(name)
                .addField("Information:", getInformation(), false)
                .addField("Stats:", getStats(), false)
                .setColor(Util.getColor(ownerId))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    private String getInformation() {
        return "- **Owner:** " + ownerTag + "\n" +
                "- **" + size() + "** members\n" +
                "- **Created: ** <t:" + createdTime + ":D>\n" +
                "- **State:** " + (isPublic ? "public" : "private");
    }

    private String getStats() {
        return "- " + minigamesWonTotal + " minigames won total\n" +
                "- " + minigamesWonCurrentWeek + " minigames won this week\n" +
                "- "; //TODO: display boss progression
    }

    /**
     * Returns a {@link MessageEmbed} containing the members of the guild.
     *
     * @param user User to include on footer
     */
    public MessageEmbed getMembersMessage(User user) {
        return new EmbedBuilder()
                .setTitle(name)
                .addField("Owner:", "<@" + ownerId + ">", false)
                .addField(getMembersField())
                .setColor(Util.getColor(ownerId))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    private MessageEmbed.Field getMembersField() {
        if (members.isEmpty()) return new MessageEmbed.Field("Members:", "0 members", false);

        StringBuilder membersString = new StringBuilder();
        boolean newLine = false;

        for (Pair<Long, String> member : members) {
            if (newLine) membersString.append('\n');

            membersString.append(member.second);
            if (elders.contains(member.first)) {
                membersString.append(" **elder**");
            }
            newLine = true;
        }

        return new MessageEmbed.Field("Members:", membersString.toString(), false);
    }

    public void transfer(long newOwnerId, String newOwnerTag) {
        removeMember(newOwnerId);
        members.add(new Pair<>(ownerId, ownerTag));

        ownerId = newOwnerId;
        ownerTag = newOwnerTag;
    }

    public void promote(long memberId) {
        elders.add(memberId);
    }

    public void demote(long memberId) {
        elders.remove(memberId);
    }

    public boolean isElder(long memberId) {
        return elders.contains(memberId);
    }

    public boolean isElderOrOwner(long memberId) {
        return ownerId == memberId || isElder(memberId);
    }

    /**
     * Adds member to the guild.
     *
     * @param userId Id of the member
     * @param tag    Tag of the member
     */
    public void addMember(long userId, String tag) {
        members.add(new Pair<>(userId, tag));
    }

    /**
     * Removes a member from the guild.
     * Doesn't remove the owner.
     *
     * @param userId Id of the member
     */
    public void removeMember(long userId) {
        members.removeIf(memberPair -> memberPair.first == userId);
        elders.remove(userId);
    }

    /**
     * Returns true if the user is in the invite list.
     *
     * @param memberId Id of the user
     */
    public boolean isInvited(long memberId) {
        return invites.contains(memberId);
    }

    /**
     * Adds the user to the invite list.
     *
     * @param memberId Id of the user
     */
    public void invite(long memberId) {
        invites.add(memberId);
        Util.schedule(() -> invites.remove(memberId), 60000);
    }

    public void rename(String name) {
        this.name = name;
    }

    /**
     * A public party allows anyone to join the party.
     * The default state for a new guild is public.
     * The privacy can only be changed by the owner.
     * <p>
     * A private party requires the owner or any elder to invite each user before they can join.
     *
     * @return Is the party public.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Sets the guild publicity.
     *
     * @see Guild#isPublic() for the difference between public and private party.
     */
    public void setPublicity(boolean toPublic) {
        isPublic = toPublic;
    }

    public void minigameWon() {
        minigamesWonTotal++;
        minigamesWonCurrentWeek++;
    }

    public void onNewWeek() {
        minigamesWonCurrentWeek = 0;
    }

}
