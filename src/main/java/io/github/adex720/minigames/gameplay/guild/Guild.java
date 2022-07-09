package io.github.adex720.minigames.gameplay.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.data.IdCompound;
import io.github.adex720.minigames.data.JsonSavable;
import io.github.adex720.minigames.gameplay.guild.boss.GuildBoss;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Triple;
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
public class Guild implements JsonSavable, IdCompound { //TODO: record member join date

    public static final int MAX_NAME_LENGTH = 15;
    public static final char[] INVALID_NAME_CHARACTERS = {'@', '\\'};

    public static final int MAX_SIZE = 10;

    private long ownerId;
    private String ownerTag;
    private long ownerJoined;
    private final Set<Triple<Long, String, Long>> members; // id, tag, join-time
    private String name;

    private final Set<Long> elders;
    private final Set<Long> invites;
    private boolean isPublic;

    private final long createdTime;

    private int minigamesWonTotal;
    private int minigamesWonCurrentWeek;

    private GuildBoss boss;

    public Guild(MinigamesBot bot, long ownerId,String ownerTag, String name) {
        createdTime = System.currentTimeMillis();

        this.ownerId = ownerId;
        this.ownerTag = ownerTag;
        this.ownerJoined = createdTime;
        members = new HashSet<>();
        this.name = name;

        this.elders = new HashSet<>();
        this.invites = new HashSet<>();

        isPublic = true;

        minigamesWonTotal = 0;
        minigamesWonCurrentWeek = 0;

        boss = bot.getGuildBossList().get(0);
    }

    public Guild(long ownerId, String ownerTag, long ownerJoined, ArrayList<Triple<Long, String, Long>> members, ArrayList<Long> elders, String name,
                 long created, boolean isPublic, int minigamesWonTotal, int minigamesWonCurrentWeek, GuildBoss boss) {

        this.ownerId = ownerId;
        this.ownerTag = ownerTag;
        this.ownerJoined = ownerJoined;

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

        this.boss = boss;
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

    public static Guild fromJson(JsonObject json, MinigamesBot bot) {
        long ownerId = JsonHelper.getLong(json, "owner");
        String ownerTag = JsonHelper.getString(json, "owner-tag");
        long ownerJoined = JsonHelper.getLong(json, "owner-joined", System.currentTimeMillis());

        JsonArray membersJson = JsonHelper.getJsonArrayOrEmpty(json, "members");
        ArrayList<Triple<Long, String, Long>> members = new ArrayList<>();
        ArrayList<Long> elders = new ArrayList<>();

        for (JsonElement jsonElement : membersJson) {
            JsonObject memberJson = jsonElement.getAsJsonObject();

            long userId = JsonHelper.getLong(memberJson, "id");
            String tag = JsonHelper.getString(memberJson, "tag");
            long joined = JsonHelper.getLong(memberJson, "join", System.currentTimeMillis());
            members.add(new Triple<>(userId, tag, joined));

            if (memberJson.has("elder")) elders.add(userId);
        }

        String name = JsonHelper.getString(json, "name");

        boolean isPublic = JsonHelper.getBoolean(json, "public");
        long created = JsonHelper.getLong(json, "created");

        int minigamesWonTotal = JsonHelper.getInt(json, "wins");
        int minigamesWonCurrentWeek = JsonHelper.getInt(json, "wins-week");

        GuildBoss boss;
        if (json.has("boss")) {
            JsonObject bossJson = JsonHelper.getJsonObject(json, "boss");
            boss = GuildBoss.fromJson(bossJson);
        } else {
            boss = bot.getGuildBossList().get(0);
        }

        return new Guild(ownerId, ownerTag, ownerJoined,members, elders, name, created, isPublic, minigamesWonTotal, minigamesWonCurrentWeek, boss);
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

        json.add("boss", boss.asJson());

        return json;
    }

    private JsonArray getMembersJson() {
        JsonArray json = new JsonArray();

        for (Triple<Long, String, Long> member : members) {
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

        for (Triple<Long, String, Long> member : members) {
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
        for (Triple<Long, String, Long> member : members) {
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

        for (Triple<Long, String, Long> member : members) {
            if (newLine) membersString.append('\n');

            membersString.append(member.second);
            if (elders.contains(member.first)) {
                membersString.append(" **elder**");
            }
            newLine = true;
        }

        return new MessageEmbed.Field("Members:", membersString.toString(), false);
    }

    public void transfer(MinigamesBot bot, long newOwnerId, String newOwnerTag, long newOwnerJoined) {
        removeMember(newOwnerId);
        members.add(new Triple<>(ownerId, ownerTag, ownerJoined));

        ownerId = newOwnerId;
        ownerTag = newOwnerTag;
        ownerJoined = newOwnerJoined;

        onTransfer(bot);
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
    public void addMember(long userId, String tag, long timestamp) {
        members.add(new Triple<>(userId, tag, timestamp));
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
     * Returns the ids of all members.
     */
    public Set<Long> getMemberIds() {
        Set<Long> memberIds = new HashSet<>();
        memberIds.add(ownerId);

        for (Triple<Long, String, Long> member : members) {
            memberIds.add(member.first);
        }

        return memberIds;
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

    /**
     * Appends stats and damages boss.
     */
    public void minigameWon(MinigamesBot bot) {
        minigamesWonTotal++;
        minigamesWonCurrentWeek++;

        damageBoss(bot);
    }

    /**
     * Damages the boss and checks if it's beaten.
     */
    public void damageBoss(MinigamesBot bot) {
        boss.damage();
        if (boss.isDead()) {
            boss.reward.apply(bot, this);

            boss = bot.getGuildBossList().get(boss.id + 1);
        }
    }

    /**
     * Resets weekly progress and guild bosses.
     */
    public void onNewWeek(MinigamesBot bot) {
        minigamesWonCurrentWeek = 0;
        boss = bot.getGuildBossList().get(0);
    }

    /**
     * Creates a {@link MessageEmbed} containing information about the current {@link GuildBoss}.
     *
     * @param user User to use on footer.
     */
    public MessageEmbed getBossMessage(User user) {
        return new EmbedBuilder()
                .setTitle(name + ": Guild boss")
                .addField(boss.getInfoField())
                .setColor(boss.color)
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();

    }

    public void onDelete(MinigamesBot bot) {
        for (long memberId : getMemberIds()) {
            bot.getProfileManager().getProfile(memberId).guildLeft();
        }
    }

    /**
     * Call this after changing the owner id
     */
    public void onTransfer(MinigamesBot bot) {
        for (long memberId : getMemberIds()) {
            bot.getProfileManager().getProfile(memberId).guildJoined(ownerId);
        }
    }
}
