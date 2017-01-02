/**
 * 
 */
package org.bibliome.util.filters;



/**
 * A filter that accepts nothing.
 * 
 * @author rbossy
 */
public class RejectAll<T> implements Filter<T> {
    @Override
    public boolean accept(T x) {
        return false;
    }
}
