/**
 * 
 */
package org.bibliome.util.filters;



/**
 * A filter that accepts everything.
 * 
 * @author rbossy
 */
public class AcceptAll<T> implements Filter<T> {
    @Override
    public boolean accept(T x) {
        return true;
    }
}
