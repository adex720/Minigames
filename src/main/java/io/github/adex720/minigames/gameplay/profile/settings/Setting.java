package io.github.adex720.minigames.gameplay.profile.settings;

import io.github.adex720.minigames.MinigamesBot;

import java.util.Objects;

public record Setting(String name, String description, int id, boolean defaultValue) {

    public void init(MinigamesBot bot) {
        bot.getSettingsList().add(this);
        bot.getCommandManager().commandSettings.addSetting(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Setting) obj;
        return Objects.equals(this.name, that.name) &&
                this.id == that.id &&
                this.defaultValue == that.defaultValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, defaultValue);
    }

    @Override
    public String toString() {
        return "Setting[" +
                "name=" + name + ", " +
                "id=" + id + ", " +
                "defaultValue=" + defaultValue + ']';
    }


}
