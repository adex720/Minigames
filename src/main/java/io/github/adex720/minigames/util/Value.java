package io.github.adex720.minigames.util;

/**
 * A contains a value.
 * This class is useful for lambdas requiring final or efficiently final variables
 *
 * @author adex720
 */
public class Value<T> {

    public Value(T value) {
        this.value = value;
    }

    public T value;

}
