package io.github.adex720.minigames.util;

public class Triple<F, S, T> {

    public F first;
    public S second;
    public T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public int hashCode() {

        if (first != null) {
            if (second != null) {
                if (third != null) return first.hashCode() ^ second.hashCode() ^ third.hashCode();
                return first.hashCode() ^ second.hashCode();
            }

            if (third != null) return first.hashCode() ^ third.hashCode();
            return first.hashCode();
        }

        if (second != null) {
            if (third != null) return second.hashCode() ^ third.hashCode();
            else return second.hashCode();
        }

        if (third != null) return third.hashCode();
        else return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Triple pair) {
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
