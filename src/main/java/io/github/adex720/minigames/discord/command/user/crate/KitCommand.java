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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class KitCommand extends Command {

    public static final int NO_COINS = 0;
    public static final int NO_CRATE = -1;

    public final int kitId;
    public static int KITS_COUNT = 0;

    private final int rewardCoins; // 0 if none
    private final int crateId; // -1 if none

    private final int cooldownHours;
    private final int cooldownMillis;

    private final HashMap<Long, OffsetDateTime> COOLDOWNS;

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
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        int canClaim = canClaim(event, ci);
        if (canClaim == 0) {
            OffsetDateTime ready = COOLDOWNS.get(ci.authorId()); // Getting times
            OffsetDateTime current = event.getTimeCreated();
            if (ready != null) {
                if (ready.isAfter(current)) {
                    event.getHook().sendMessage("This booster is on cooldown for " + getCooldown(ready, current) + ".").queue();
                    return true;
                }
            }

            Profile profile = ci.profile();
            addReward(event, profile);

            startCooldown(ci.authorId(), current);
            event.getHook().sendMessage("You claimed your " + name + " booster. You received " + rewardCoins + " coins.").queue();

            profile.appendQuests(quest -> quest.kitClaimed(event, name, profile));
        } else {
            event.getHook().sendMessage(permissionCheck.getFailMessage(event, ci, canClaim)).queue();
        }
        return true;
    }

    public Pair<Integer, Integer> addRewardAndCooldown(SlashCommandEvent event, Profile profile, OffsetDateTime time) {
        startCooldown(profile.getId(), time);
        return addReward(event, profile);
    }

    /**
     * Doesn't start cooldown
     */
    public Pair<Integer, Integer> addReward(SlashCommandEvent event, Profile profile) {
        if (rewardCoins > 0) {
            profile.addCoins(rewardCoins, true, event);
        } else {
            profile.addCrate(crateId);
        }

        return new Pair<>(rewardCoins, crateId);
    }

    public String getCooldown(OffsetDateTime ready, OffsetDateTime current) {
        return Util.formatTime(Duration.between(current, ready));
    }

    public String getCooldownFull(long userId, OffsetDateTime current) {
        OffsetDateTime ready = COOLDOWNS.get(userId);
        if (ready != null) {
            if (ready.isAfter(current)) {
                return bot.getEmote("kit_loading") + " **" + name + "**: " + getCooldown(ready, current);
            }
        }

        return bot.getEmote("kit_ready") + " " + name + ": Ready";
    }

    public boolean isOnCooldown(long userId, OffsetDateTime time) {
        if (!COOLDOWNS.containsKey(userId)) return false;

        return COOLDOWNS.get(userId).isAfter(time);
    }

    private void startCooldown(long userId, OffsetDateTime used) {
        COOLDOWNS.put(userId, used.plusHours(cooldownHours));

        bot.addTimerTask(() -> COOLDOWNS.remove(userId, used), cooldownMillis, false);
    }

    public void clearCooldown(long userId) {
        COOLDOWNS.remove(userId);
    }

    public interface PermissionCheck {
        /**
         * @return 0 if booster can be used.
         * Different positive return values are used to indicate reply message.
         */
        int canUse(SlashCommandEvent event, CommandInfo ci);

        /**
         * @param reason reply message id specified by {@link KitCommand.PermissionCheck#canUse(SlashCommandEvent, CommandInfo)}
         * @return a specific fail message reasoning why the kit can't be claimed.
         */
        String getFailMessage(SlashCommandEvent event, CommandInfo ci, int reason);
    }

    /**
     * Returns 0 if booster can be used.
     * Positive return values are used to indicate reply message.
     */
    public int canClaim(SlashCommandEvent event, CommandInfo ci) {
        return permissionCheck.canUse(event, ci);
    }

    public String getDefaultCooldown() {
        return cooldownHours + " hours";
    }


    public static class Criterion {

        public static final PermissionCheck ALWAYS = new PermissionCheck() {
            @Override
            public int canUse(SlashCommandEvent event, CommandInfo ci) {
                return 0;
            }

            @Override
            public String getFailMessage(SlashCommandEvent event, CommandInfo ci, int reason) {
                return "";
            }
        };

        public static final PermissionCheck IN_SUPPORT_SERVER = new PermissionCheck() {

            @Override
            public int canUse(SlashCommandEvent event, CommandInfo ci) {
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
            public String getFailMessage(SlashCommandEvent event, CommandInfo ci, int reason) {
                if (reason == 1) {
                    return "This booster can only be claimed on the support server.\n" + CommandServer.SERVER_LINK;
                }
                if (reason == 2) {
                    return "You need to be on this server for 24 hours to claim this booster. This is to ensure people leaving after claiming the booster and then leaving.";
                }

                return ""; // never reached
            }
        };

    }
}
