package io.github.adex720.minigames.gameplay.manager.kit;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.user.crate.KitCommand;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.util.ArrayList;

/**
 * Manages kits.
 *
 * @author adex720
 */
public class KitCooldownManager extends Manager {

    public KitCooldownManager(MinigamesBot bot) {
        super(bot, "kit-manager");
        KIT_COMMANDS = new ArrayList<>(5);
    }

    private final ArrayList<KitCommand> KIT_COMMANDS;

    public void addKit(KitCommand kitCommand) {
        KIT_COMMANDS.add(kitCommand);
    }

    public KitCommand getKitCommand(int id) {
        return KIT_COMMANDS.get(id);
    }

    public ArrayList<KitCommand> getKits() {
        return KIT_COMMANDS;
    }

    /**
     * Clears the cooldown of each kit from the given user
     * */
    public void clearCooldowns(long userId) {
        KIT_COMMANDS.forEach(k -> k.clearCooldown(userId));
    }
}
