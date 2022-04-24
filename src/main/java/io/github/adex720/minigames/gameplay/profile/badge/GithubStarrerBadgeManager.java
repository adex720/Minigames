package io.github.adex720.minigames.gameplay.profile.badge;

import io.github.adex720.minigames.util.network.ConnectionApp;
import net.dv8tion.jda.internal.requests.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GithubStarrerBadgeManager {

    private final HashSet<String> STARRERS;

    public GithubStarrerBadgeManager() {
        STARRERS = new HashSet<>();
    }

    public boolean isValidForEmote(long id) {
        return false;
    }

    private void reloadStarrers() {

    }

    private void isConnected() {
        //TODO: set correct user
        Route.CompiledRoute route = Route.Self.GET_CONNECTIONS.compile();

        List<ConnectionApp> connectionApps = new ArrayList<>();


    }


}
