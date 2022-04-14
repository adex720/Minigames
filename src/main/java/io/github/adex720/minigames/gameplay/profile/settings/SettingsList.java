package io.github.adex720.minigames.gameplay.profile.settings;

import io.github.adex720.minigames.MinigamesBot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class SettingsList {

    public final Setting CLAIM_QUESTS;

    private final HashMap<String, Setting> SETTINGS_BY_NAME;
    private final HashMap<Integer, Setting> SETTINGS_BY_ID;

    public SettingsList() {
        SETTINGS_BY_NAME = new HashMap<>();
        SETTINGS_BY_ID = new HashMap<>();

        CLAIM_QUESTS = new Setting("claim-quests", "Starts new quests one /claim.", 0, true);
    }

    public void init(MinigamesBot bot) {
        CLAIM_QUESTS.init(bot);
    }

    public void add(Setting setting) {
        SETTINGS_BY_NAME.put(setting.name(), setting);
        SETTINGS_BY_ID.put(setting.id(), setting);
    }

    public Setting get(String name) {
        return SETTINGS_BY_NAME.get(name);
    }

    public Setting get(int id) {
        return SETTINGS_BY_ID.get(id);
    }

    /**
     * Not the fastest, only used when registering /settings
     */
    public ArrayList<Setting> getAll() {
        ArrayList<Setting> settings = new ArrayList<>(SETTINGS_BY_ID.values());
        settings.sort(Comparator.comparingInt(Setting::id));
        return settings;
    }

}
