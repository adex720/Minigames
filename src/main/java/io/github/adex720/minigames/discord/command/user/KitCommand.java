package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandInvite;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandServer;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class KitCommand extends Command {

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
                return 1;
            }
            if (event.getMember().hasTimeJoined()) {
                if (event.getMember().getTimeJoined().isAfter(OffsetDateTime.now().minusDays(1))) {
                    return 2;
                }
            }

            return 0;
        }

        @Override
        public String getFailMessage(SlashCommandEvent event, CommandInfo ci, int reason) {
            if (reason == 1) {
                return "This kit can only be claimed on the support server.\n" + CommandInvite.INVITE_LINK;
            }
            if (reason == 2) {
                return "You need to be on this server for 24 hours to claim this kit. This is to ensure people leaving after claiming the kit and then leaving.";
            }

            return "";
        }
    };

    private final int reward;
    private final int cooldownHours;
    private final int cooldownMillis;

    private final HashMap<Long, OffsetDateTime> COOLDOWNS;

    private final PermissionCheck permissionCheck;

    public KitCommand(MinigamesBot bot, String name, String description, int reward, int cooldownHours, PermissionCheck permissionCheck) {
        super(bot, name, description, CommandCategory.USER);
        requiresProfile();
        COOLDOWNS = new HashMap<>();

        this.reward = reward;
        this.cooldownHours = cooldownHours;
        cooldownMillis = cooldownHours * 60 * 60 * 1000;

        this.permissionCheck = permissionCheck;
    }

    public KitCommand(MinigamesBot bot, String name, int reward, int cooldownHours) {
        this(bot, name, "Gives " + reward + " coins every " + cooldownHours + " hours.", reward, cooldownHours, ALWAYS);
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        int canClaim = canClaim(event, ci);
        if (canClaim == 0) {
            OffsetDateTime ready = COOLDOWNS.get(ci.authorId());
            OffsetDateTime current = event.getTimeCreated();
            if (ready != null) {
                if (ready.isAfter(current)) {
                    event.getHook().sendMessage("This kit is on cooldown for " + Util.formatTime(Duration.between(current, ready)) + ".").queue();
                    return true;
                }
            }

            Profile profile = ci.profile();
            profile.addCoins(reward, true);
            startCooldown(ci.authorId(), current);
            event.getHook().sendMessage("You claimed your " + name + " kit. You received " + reward + " coins.").queue();

            profile.appendQuests(quest -> quest.kitClaimed(name, profile));
        } else {
            event.getHook().sendMessage(permissionCheck.getFailMessage(event, ci, canClaim)).queue();
        }
        return true;
    }

    private void startCooldown(long userId, OffsetDateTime used) {
        COOLDOWNS.put(userId, used.plusHours(cooldownHours));

        bot.addTimerTask(() -> COOLDOWNS.remove(userId, used), cooldownMillis, false);
    }

    public interface PermissionCheck {
        /**
         * Returns 0 if kit can be used.
         * Positive return values are used to indicate reply message.
         */
        int canUse(SlashCommandEvent event, CommandInfo ci);

        String getFailMessage(SlashCommandEvent event, CommandInfo ci, int reason);
    }

    /**
     * Returns 0 if kit can be used.
     * Positive return values are used to indicate reply message.
     */
    public int canClaim(SlashCommandEvent event, CommandInfo ci) {
        return permissionCheck.canUse(event, ci);
    }

    public String getDefaultCooldown() {
        return cooldownHours + " hours";
    }
}
