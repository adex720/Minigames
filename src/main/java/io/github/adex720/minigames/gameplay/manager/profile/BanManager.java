package io.github.adex720.minigames.gameplay.manager.profile;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.util.HashSet;
import java.util.Set;

public class BanManager extends Manager {

    private final Set<Long> BANS;

    public BanManager(MinigamesBot bot) {
        super(bot, "ban-manager");

        BANS = new HashSet<>();
    }

    public void ban(long id) {
        BANS.add(id);

        bot.getProfileManager().ban(id);
    }

    /**
     * @return true if the user was banned.
     * */
    public boolean unban(long id) {
        if (BANS.remove(id)) {
            bot.getProfileManager().unban(id);
            return true;
        }
        return false;
    }

    public boolean isBanned(long id) {
        return BANS.contains(id);
    }


}
