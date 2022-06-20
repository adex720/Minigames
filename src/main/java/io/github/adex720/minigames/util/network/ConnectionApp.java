package io.github.adex720.minigames.util.network;

import net.dv8tion.jda.api.utils.data.DataObject;

/**
 * @author adex720
 */
public class ConnectionApp {

    public final String app;
    public final String username;

    public ConnectionApp(String app, String username) {
        this.app = app;
        this.username = username;
    }

    public static ConnectionApp fromJson(DataObject json) {
        return new ConnectionApp(json.getString("type"), json.getString("name"));
    }

    @Override
    public String toString() {
        return app;
    }
}
