package io.github.adex720.minigames.util;

/**
 * Converts an Object from one type to another.
 *
 * @param <I> Type of input
 * @param <O> Type of output
 * @author adex720
 */
@FunctionalInterface
public interface Convertor<I, O> {
    O convert(I input);
}
