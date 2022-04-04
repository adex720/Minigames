package io.github.adex720.minigames.gameplay.profile.booster;

public class Booster {

    public final BoosterRarity rarity;
    public final long expiration;

    public Booster(BoosterRarity rarity, long expiration) {
        this.rarity = rarity;
        this.expiration = expiration;
    }
}
