package io.github.adex720.minigames.gameplay.profile;

public record Badge(int id, String name, String emojiName) {

    public String getEmoji() {
        return emojiName;
    }

}
