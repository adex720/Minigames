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

    public static final int MAX_SIZE = 10;

    private long ownerId;
    private String ownerTag;
    private final Set<Pair<Long, String>> members;
    private String name;

    private final long createdTime;

    private int minigamesWonTotal;
    private int minigamesWonCurrentWeek;

    public Guild(long ownerId, String name) {
        createdTime = System.currentTimeMillis();

        this.ownerId = ownerId;
        members = new HashSet<>();
        this.name = name;

        minigamesWonTotal = 0;
        minigamesWonCurrentWeek = 0;
    }

    public Guild(long ownerId, ArrayList<Pair<Long, String>> members, String name, long created, int minigamesWonTotal, int minigamesWonCurrentWeek) {
        this.ownerId = ownerId;

        this.members = new HashSet<>();
        this.members.addAll(members);
        this.name = name;

        this.createdTime = created;

        this.minigamesWonTotal = minigamesWonTotal;
        this.minigamesWonCurrentWeek = minigamesWonCurrentWeek;
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

        JsonArray membersJson = JsonHelper.getJsonArrayOrEmpty(json, "members");
        ArrayList<Pair<Long, String>> members = new ArrayList<>();

        for (JsonElement jsonElement : membersJson) {
            JsonObject memberJson = jsonElement.getAsJsonObject();

            long userId = JsonHelper.getLong(memberJson, "id");
            String tag = JsonHelper.getString(memberJson, "tag");

            members.add(new Pair<>(userId, tag));
        }

        String name = JsonHelper.getString(json, "name");
        long created = JsonHelper.getLong(json, "created");

        int minigamesWonTotal = JsonHelper.getInt(json, "wins");
        int minigamesWonCurrentWeek = JsonHelper.getInt(json, "wins-week");

        return new Guild(ownerId, members, name, created, minigamesWonTotal, minigamesWonCurrentWeek);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();

        json.addProperty("owner", ownerId);
        if (!members.isEmpty()) json.add("members", getMembersJson());

        json.addProperty("name", name);
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
     * Returns a {@link MessageEmbed} containing information about the guild.
     */
    public MessageEmbed getInfoMessage() {
        return new EmbedBuilder().build();
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

    /**
     * Doesn't remove the owner
     */
    public void removeMember(long userId) {
        members.removeIf(memberPair -> memberPair.first == userId);
    }

    public void rename(String name) {
        this.name = name;
    }

    public void minigameWon() {
        minigamesWonTotal++;
        minigamesWonCurrentWeek++;
    }

    public void onNewWeek() {
        minigamesWonCurrentWeek = 0;
    }

}
