package org.bibliome.util.mappers;

/**
 * A mapper transforms an object of one type to another.
 * @author rbossy
 *
 * @param <S>
 * @param <T>
 */
public interface Mapper<S, T> {
    T map(S x);
}
