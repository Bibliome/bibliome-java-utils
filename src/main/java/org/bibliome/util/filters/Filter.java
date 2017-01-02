package org.bibliome.util.filters;


/**
 * A filter filters objects.
 */
public interface Filter<T> {

    /**
     * Checks if the specified object is accepted by this filter.
     */
    public abstract boolean accept(T x);
}
