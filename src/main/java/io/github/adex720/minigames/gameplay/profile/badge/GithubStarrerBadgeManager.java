package io.github.adex720.minigames.gameplay.profile.badge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.network.ConnectionApp;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.internal.requests.Route;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Calculates the users who should have GitHub starrer badge.
 *
 * @author adex720
 */
public class GithubStarrerBadgeManager extends Manager {

    private final HashSet<String> STARRERS;

    public GithubStarrerBadgeManager(MinigamesBot bot) {
        super(bot, "starrer-manager");
        STARRERS = new HashSet<>();
    }

    public boolean isValidForEmote(long id) {
        String username;
        try {
            username = getGithubUsername(id);
        } catch (RateLimitedException e) {
            return false;
        }
        if (username == null) return false; // GitHub account is not linked on Discord profile.

        return STARRERS.contains(username);
    }

    private void updateStarrers() {
        try {
            JsonArray response = bot.getHttpsRequester().requestJson("https://api.github.com/repos/adex720/Minigames/stargazers").getAsJsonArray();
            STARRERS.clear(); // Doing request and converting to json before clearing list.

            for (JsonElement jsonElement : response) {
                JsonObject userJson = (JsonObject) jsonElement;

                String name = JsonHelper.getString(userJson, "login");
                STARRERS.add(name);
            }
        } catch (IOException e) {
            bot.getLogger().error("Failed to reload repository starrers: {}", e.getMessage());
        }
    }

    /**
     * Gets the username of the user on GitHub using the user's connections.
     *
     * @param userId id of the user to check.
     * @return Username of the user on GitHub. Null if GitHub account is not connected.
     * @throws RateLimitedException if ratelimit is hit
     */
    private String getGithubUsername(long userId) throws RateLimitedException {
        //TODO: set correct user
        Route.CompiledRoute route = Route.Self.GET_CONNECTIONS.compile();

        List<ConnectionApp> connectionApps = bot.getHttpsRequester().makeDiscordApiRequest(bot.getJda(), ConnectionApp::fromJson, route);

        if (connectionApps.isEmpty()) return null;

        for (ConnectionApp connectionApp : connectionApps) {
            if (connectionApp.app.equals("GitHub")) {
                return connectionApp.username;
            }
        }

        return null;
    }


}
