package io.github.adex720.minigames.util;

/**
 * @author adex720
 */
public class Pair<F, S> {

    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {

        if (first != null && second != null) return first.hashCode() ^ second.hashCode();
        if (first != null) return first.hashCode();
        if (second != null) return second.hashCode();

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair pair) {
            if (!pair.first.equals(this.first)) return false;
            return pair.second.equals(this.second);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{first: " + first.toString() + ", second: " + second.toString() + "}";
    }
}
