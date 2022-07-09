package io.github.adex720.minigames.discord.command.user.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandServer;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.OffsetDateTime;
import java.util.HashMap;

/**
 * @author adex720
 */
public class KitCommand extends Command {

    public static final int NO_COINS = 0;
    public static final int NO_CRATE = -1;

    public final int kitId;
    public static int KITS_COUNT = 0;

    private final int rewardCoins; // 0 if none
    private final int crateId; // -1 if none

    private final int cooldownHours;
    private final int cooldownMillis;

    private final HashMap<Long, Long> COOLDOWNS;

    private final PermissionCheck permissionCheck;

    /**
     * A kit can't give both coins and crates
     */
    public KitCommand(MinigamesBot bot, String name, String description, int coins, int crate, int cooldownHours, PermissionCheck permissionCheck) {
        super(bot, name, description, CommandCategory.USER);
        requiresProfile();
        COOLDOWNS = new HashMap<>();

        this.kitId = KITS_COUNT++;
        bot.getKitCooldownManager().addKit(this);

        this.rewardCoins = coins;
        this.crateId = crate;
        this.cooldownHours = cooldownHours;
        cooldownMillis = cooldownHours * 60 * 60 * 1000;

        this.permissionCheck = permissionCheck;
    }

    public KitCommand(MinigamesBot bot, String name, int coins, int crate, int cooldownHours) {
        this(bot, name, getDescription(coins, crate, cooldownHours), coins, crate, cooldownHours, Criterion.ALWAYS);
    }

    public static String getDescription(int coins, int crate, int cooldownHours) {
        if (coins > 0) {
            return "Gives " + coins + " coins every " + cooldownHours + " hours.";
        }

        CrateType crateType = CrateType.get(crate);
        return "Gives " + crateType.getNameWithArticle() + " crate every " + cooldownHours + " hours.";
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Replyable replyable = Replyable.from(event);

        int canClaim = canClaim(event, ci);
        if (canClaim == 0) {
            Long ready = COOLDOWNS.get(ci.authorId()); // Getting time
            long current = Util.getEpoch(event);
            if (ready != null) {
                if (ready > current) {
                    replyable.reply("This kit is on cooldown for " + getCooldown(ready, current) + ".");
                    return true;
                }
            }

            Profile profile = ci.profile();
            addReward(replyable, profile);

            startCooldown(ci.authorId(), current);
            String reward = "";
            if (rewardCoins > 0) {
                String and = crateId >= 0 ? " and " : ".";
                reward = "You received " + rewardCoins + " coins" + and;
            }

            if (crateId >= 0) {
                CrateType crateType = CrateType.get(crateId);
                reward += crateType.getNameWithArticle() + " crate.";
            }

            replyable.reply("You claimed your " + name + " kit. You received " + reward);

            profile.appendQuests(quest -> quest.kitClaimed(replyable, name, profile));
        } else {
            replyable.reply(permissionCheck.getFailMessage(event, ci, canClaim));
        }
        return true;
    }

    public Pair<Integer, Integer> addRewardAndCooldown(SlashCommandInteractionEvent event, Profile profile, long time) {
        startCooldown(profile.getId(), time);
        return addReward(Replyable.from(event), profile);
    }

    /**
     * Doesn't start cooldown
     */
    public Pair<Integer, Integer> addReward(Replyable replyable, Profile profile) {
        if (rewardCoins > 0) {
            profile.addCoins(rewardCoins, true, replyable);
        } else {
            profile.addCrate(crateId);
        }

        return new Pair<>(rewardCoins, crateId);
    }

    public String getCooldown(long ready, long current) {
        return Util.formatTime((int) ((ready - current) * 0.001f));
    }

    public String getCooldownFull(long userId, long current) {
        Long ready = COOLDOWNS.get(userId);
        if (ready != null) {
            if (ready > current) {
                return bot.getEmote("kit_loading") + " **" + name + "**: " + getCooldown(ready, current);
            }
        }

        return bot.getEmote("kit_ready") + " " + name + ": Ready";
    }

    public boolean isOnCooldown(long userId, long time) {
        if (!COOLDOWNS.containsKey(userId)) return false;

        return COOLDOWNS.get(userId) > time;
    }

    private void startCooldown(long userId, long used) {
        COOLDOWNS.put(userId, used + cooldownMillis);

        bot.addTimerTask(() -> COOLDOWNS.remove(userId, used), cooldownMillis, false);
    }

    public void clearCooldown(long userId) {
        COOLDOWNS.remove(userId);
    }

    public interface PermissionCheck {
        /**
         * @return 0 if kit can be claimed.
         * Different positive return values are used to indicate reply message.
         */
        int canUse(SlashCommandInteractionEvent event, CommandInfo ci);

        /**
         * @param reason reply message id specified by {@link KitCommand.PermissionCheck#canUse(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent, CommandInfo)}
         * @return a specific fail message reasoning why the kit can't be claimed.
         */
        String getFailMessage(SlashCommandInteractionEvent event, CommandInfo ci, int reason);
    }

    /**
     * @return 0 if kit can be claimed.
     * Positive return values are used to indicate reply message.
     */
    public int canClaim(SlashCommandInteractionEvent event, CommandInfo ci) {
        return permissionCheck.canUse(event, ci);
    }

    public String getDefaultCooldown() {
        return cooldownHours + " hours";
    }


    public static class Criterion {

        public static final PermissionCheck ALWAYS = new PermissionCheck() {
            @Override
            public int canUse(SlashCommandInteractionEvent event, CommandInfo ci) {
                return 0;
            }

            @Override
            public String getFailMessage(SlashCommandInteractionEvent event, CommandInfo ci, int reason) {
                return "";
            }
        };

        public static final PermissionCheck IN_SUPPORT_SERVER = new PermissionCheck() {

            @Override
            public int canUse(SlashCommandInteractionEvent event, CommandInfo ci) {
                if (event.getGuild().getIdLong() != CommandServer.SERVER_ID) {
                    // Wrong server
                    return 1;
                }
                if (event.getMember().hasTimeJoined()) {
                    if (event.getMember().getTimeJoined().isAfter(OffsetDateTime.now().minusDays(1))) {
                        // User has been on the server for less than 24 hours.
                        // This is checked so people wouldn't just join the server, claim the kit and leave right after.
                        return 2;
                    }
                }

                return 0; // kit can be claimed
            }

            @Override
            public String getFailMessage(SlashCommandInteractionEvent event, CommandInfo ci, int reason) {
                if (reason == 1) {
                    return "This kit can only be claimed on the support server.\n" + CommandServer.SERVER_LINK;
                }
                if (reason == 2) {
                    return "You need to be on this server for 24 hours to claim this kit. This is to prevent people from leaving after claiming the kit and rejoining after 24 hours.";
                }

                return ""; // never reached
            }
        };

    }
}
