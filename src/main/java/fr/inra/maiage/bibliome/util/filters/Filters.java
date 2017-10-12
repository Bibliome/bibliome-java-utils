/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.filters;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Filter utilities.
 * @author rbossy
 *
 */
public abstract class Filters {
    private static final class FilteredIterator<T,U extends T> implements Iterator<U> {
        private final Filter<T> filter;
        private final Iterator<U> matrix;
        private U next;
        
        public FilteredIterator(Filter<T> filter, Iterator<U> matrix) {
            super();
            this.filter = filter;
            this.matrix = matrix;
            next = searchNext();
        }

        private U searchNext() {
            while (matrix.hasNext()) {
                U e = matrix.next();
                if (filter.accept(e))
                    return e;
            }
            return null;
        }
        
        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public U next() {
            if (next == null)
                throw new NoSuchElementException();
            U result = next;
            next = searchNext();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static final class DeParamFilter<T,P> implements Filter<T> {
    	private final ParamFilter<T,P> paramFilter;
    	private final P param;

		private DeParamFilter(ParamFilter<T,P> paramFilter, P param) {
			super();
			this.paramFilter = paramFilter;
			this.param = param;
		}

		@Override
		public boolean accept(T x) {
			return paramFilter.accept(x, param);
		}
	}
    
    public static <T,P> Filter<T> deParam(ParamFilter<T,P> paramFilter, P param) {
    	if (paramFilter == null)
    		return null;
    	return new DeParamFilter<T,P>(paramFilter, param);
    }

    /**
     * Returns an iterator with only elements that satisfy the specified filter.
     * @param <T>
     * @param <U>
     * @param filter
     * @param it
     */
    public static final <T,U extends T> Iterator<U> apply(Filter<T> filter, Iterator<U> it) {
        if (filter == null)
            return it;
        return new FilteredIterator<T,U>(filter, it);
    }
    
    public static final <T,U extends T,P> Iterator<U> apply(ParamFilter<T,P> filter, P param, Iterator<U> it) {
    	return apply(deParam(filter, param), it);
    }

    /**
     * Selects the specified collection elements that satisfy the specified filter.
     * @param <T>
     * @param <U>
     * @param filter
     * @param source
     * @param target
     * @return the target collection
     */
    public static final <T,U extends T> Collection<U> apply(Filter<T> filter, Collection<U> source, Collection<U> target) {
        if (filter == null)
            target.addAll(source);
        else
            for (U x : source)
                if (filter.accept(x))
                    target.add(x);
        return target;
    }
    
    public static final <T,U extends T,P> Collection<U> apply(ParamFilter<T,P> filter, P param, Collection<U> source, Collection<U> target) {
    	return apply(deParam(filter, param), source, target);
    }
    
    /**
     * Returns either all elements in the specified collection satisfy the specified filter.
     * Returns true if the collection is null.
     * @param <T>
     * @param filter
     * @param c
     */
    public static final <T> boolean forall(Filter<T> filter, Collection<T> c) {
        if (c == null)
            return true;
        for (T x : c)
            if (!filter.accept(x))
                return false;
        return true;
    }
    
    public static final <T,P> boolean forall(ParamFilter<T,P> filter, P param, Collection<T> c) {
    	return forall(deParam(filter, param), c);
    }
    
    /**
     * Returns either at least one element in the specified collection satisfies the specified filter.
     * Returns false if the collection is null.
     * @param <T>
     * @param filter
     * @param c
     */
    public static final <T> boolean exists(Filter<T> filter, Collection<T> c) {
        if (filter == null)
            return false;
       for (T x : c)
            if (filter.accept(x))
                return true;
        return false;
    }
    
    public static final <T,P> boolean exists(ParamFilter<T,P> filter, P param, Collection<T> c) {
    	return exists(deParam(filter, param), c);
    }

    /**
     * Downcasts a filter.
     * @param <T>
     * @param <U>
     * @param filter
     */
    @SuppressWarnings("unchecked")
	public static final <T,U extends T> Filter<U> downcast(Filter<T> filter) {
    	return (Filter<U>) filter;
    }
    
    public static final <T,U extends T,P> Filter<U> downcast(ParamFilter<T,P> filter, P param) {
    	return downcast(deParam(filter, param));
    }
}
