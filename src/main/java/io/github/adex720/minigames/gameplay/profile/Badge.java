package io.github.adex720.minigames.gameplay.profile;

/**
 * Badge is a visual feature displayed on profiles.
 *
 * @author adex720
 * */
public record Badge(int id, String name, String emojiName) {

    public String getEmoji() {
        return emojiName;
    }

}
