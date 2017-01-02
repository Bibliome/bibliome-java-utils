/**
 * 
 */
package org.bibliome.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Cartesian product generator.
 * This class is used to iterate through the cartesian product of collections specified to the constructor of this class.
 * 
 * @author rbossy
 */
public class CartesianProduct<T> {
    private final List<Collection<T>> generators;
    private final List<Iterator<T>>       iterators;
    private final List<T>                 elements;
    private boolean                   started = false;

    /**
     * Instantiates a new cartesian product generator.
     * 
     * @param generators collections of which to compute the cartesian product
     */
    public CartesianProduct(List<Collection<T>> generators) {
        this.generators = new ArrayList<Collection<T>>(generators);
        iterators = new ArrayList<Iterator<T>>(generators.size());
        elements = new ArrayList<T>(generators.size());
        for (@SuppressWarnings("unused") Collection<T> _ : generators) {
        	iterators.add(null);
        	elements.add(null);
        }
        reset();
    }

    /**
     * Resets the cartesian product.
     */
    public void reset() {
        started = false;
    }

    /**
     * Computes the first tuple of the cartesian product.
     * @return true, if no collection operand of the product is empty
     */
    private boolean first() {
        for (int i = 0; i < generators.size(); ++i) {
            Collection<T> c = generators.get(i);
            if (c.isEmpty())
                return false;
            Iterator<T> it = c.iterator();
            elements.set(i, it.next());
            iterators.set(i, it);
        }
        return true;
    }

    /**
     * Computes the next tuple of the cartesian product.
     * @return true, if successful, false if there is no more tuple in the cartesian product
     */
    public boolean next() {
        if (!started) {
            started = true;
            return first();
        }
        for (int i = 0; i < generators.size(); ++i) {
            Iterator<T> it = iterators.get(i);
            if (it.hasNext()) {
                elements.set(i, it.next());
                return true;
            }
            it = generators.get(i).iterator();
            iterators.set(i, it);
            elements.set(i, it.next());
        }
        return false;
    }

    /**
     * Returns the current tuple of the cartesian product.
     * first() and next() will write in the returned array.
     * @return the current tuple of the cartesian product
     */
    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
