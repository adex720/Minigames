package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.miscellaneous.CommandServer;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class KitCommand extends Command {

    public static final PermissionCheck ALWAYS = (event, ci) -> true;
    public static final PermissionCheck IN_SUPPORT_SERVER = (event, ci) -> event.getGuild().getIdLong() == CommandServer.SERVER_ID;

    private final int reward;
    private final int cooldownHours;
    private final int cooldownMillis;

    private final HashMap<Long, OffsetDateTime> COOLDOWNS;

    private final PermissionCheck permissionCheck;
    private final String message;

    public KitCommand(MinigamesBot bot, String name, String description, int reward, int cooldownHours, PermissionCheck permissionCheck, String message) {
        super(bot, name, description, CommandCategory.USER);
        requiresProfile();
        COOLDOWNS = new HashMap<>();

        this.reward = reward;
        this.cooldownHours = cooldownHours;
        cooldownMillis = cooldownHours * 60 * 60 * 1000;

        this.permissionCheck = permissionCheck;
        this.message = message;
    }

    public KitCommand(MinigamesBot bot, String name, int reward, int cooldownHours) {
        this(bot, name, "Gives " + reward + " coins every " + cooldownHours + " hours.", reward, cooldownHours, ALWAYS, "");
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        if (canClaim(event, ci)) {
            OffsetDateTime ready = COOLDOWNS.get(ci.authorId());
            OffsetDateTime current = event.getTimeCreated();
            if (ready != null) {
                if (ready.isAfter(current)) {
                    event.getHook().sendMessage("This kit is on cooldown for " + Duration.between(current, ready) + ".").queue();
                    return true;
                }
            }

            ci.profile().addCoins(reward);
            startCooldown(ci.authorId(), current);
            event.getHook().sendMessage("You claimed your " + name + " kit. You received " + reward + " coins.").queue();
        } else {
            event.getHook().sendMessage(message).queue();
        }
        return true;
    }

    private void startCooldown(long userId, OffsetDateTime used) {
        COOLDOWNS.put(userId, used);

        bot.addTimerTask(() -> COOLDOWNS.remove(userId, used), cooldownMillis, false);
    }

    @FunctionalInterface
    public interface PermissionCheck {
        boolean canUse(SlashCommandEvent event, CommandInfo ci);
    }

    public boolean canClaim(SlashCommandEvent event, CommandInfo ci) {
        return permissionCheck.canUse(event, ci);
    }

    public String getDefaultCooldown() {
        return cooldownHours + " hours";
    }
}
