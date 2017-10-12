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

package fr.inra.maiage.bibliome.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import fr.inra.maiage.bibliome.util.filters.Filter;
import fr.inra.maiage.bibliome.util.filters.Filters;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.mappers.ParamMapper;

/**
 * Miscellanous iterator utilities.
 * @author rbossy
 *
 */
public abstract class Iterators {
    private final static class LoopIterable<T> implements Iterable<T> {
        private final Iterator<T> it;
        
        public LoopIterable(Iterator<T> it) {
            super();
            this.it = it;
        }

        @Override
        public Iterator<T> iterator() {
            return it;
        }
    }
    
    /**
     * Returns an iterable whose iterator() method returns the specified iterator.
     * The returned iterable can be used in only one loop.
     * @param <T>
     * @param it
     * @return an iterable whose iterator() method returns the specified iterator
     */
    public static final <T> Iterable<T> loop(Iterator<T> it) {
        return new LoopIterable<T>(it);
    }
  
    /**
     * NOP loop the specified iterator until there are no more elements.
     * @param it
     */
    public static void deplete(Iterator<?> it) {
    	while (it.hasNext())
    		it.next();
    }
    
    /**
     * Returns an empty iterator.
     * The hasNext() method of the returned iterator always returns false.
     * @param <T>
     * @return an empty iterator
     */
    public static final <T> Iterator<T> emptyIterator() {
        Collection<T> emptyCollection = Collections.emptySet();
        return emptyCollection.iterator();
    }
    
    private static final class SingletonIterator<T> implements Iterator<T> {
    	private final T x;
    	private boolean notSeen = true;
		
    	private SingletonIterator(T x) {
			super();
			this.x = x;
		}

		@Override
		public boolean hasNext() {
			return notSeen;
		}

		@Override
		public T next() {
			if (!notSeen)
				throw new NoSuchElementException();
			notSeen = false;
			return x;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
    }
    
    /**
     * Returns an iterator that iterates over a single value.
     * @param <T>
     * @param x the value
     * @return an iterator that iterates over a single value
     */
    public static final <T,U extends T> Iterator<T> singletonIterator(U x) {
    	return new SingletonIterator<T>(x);
    }
    
    public static final <T,U extends T> Iterator<T> nonNullSingleton(U x) {
    	if (x == null)
    		return emptyIterator();
    	return singletonIterator(x);
    }
    
    private static final class FlatIterator<T> implements Iterator<T> {
        private final Iterator<Iterator<T>> master;
        private Iterator<T> current = emptyIterator();

        private FlatIterator(Iterator<Iterator<T>> master) {
            super();
            this.master = master;
        }

        private void forward() {
            while (!current.hasNext()) {
                if (!master.hasNext())
                    break;
                current = master.next();
            }
        }
        
        @Override
        public boolean hasNext() {
            forward();
            return current.hasNext();
        }

        @Override
        public T next() {
            forward();
            return current.next();
        }

        @Override
        public void remove() {
            current.remove();
        }
    }
    
    /**
     * Returns an iterator that chains the elements in the specified iterators.
     * @param <T>
     * @param it
     * @return an iterator that chains the elements in the specified iterators
     */
    public static final <T> Iterator<T> flatten(Iterator<Iterator<T>> it) {
        return new FlatIterator<T>(it);
    }
    
    private static final class FlatMapperIterator<T,U> implements Iterator<U> {
    	private final Mapper<T,? extends Iterator<U>> mapper;
    	private final Iterator<? extends T> masterIterator;
    	private Iterator<U> currentIterator = emptyIterator();

		private FlatMapperIterator(Mapper<T,? extends Iterator<U>> mapper, Iterator<? extends T> masterIterator) {
			super();
			this.mapper = mapper;
			this.masterIterator = masterIterator;
		}

		@Override
		public boolean hasNext() {
			while (true) {
				if (currentIterator.hasNext())
					return true;
				if (!masterIterator.hasNext())
					return false;
				currentIterator = mapper.map(masterIterator.next());
			}
		}

		@Override
		public U next() {
			while (true) {
				if (currentIterator.hasNext())
					return currentIterator.next();
				currentIterator = mapper.map(masterIterator.next());
			}
		}

		@Override
		public void remove() {
			currentIterator.remove();
		}
    }
    
    /**
     * Map and flatten composition.
     * @param it
     * @param mapper
     */
    public static final <T,U> Iterator<U> mapAndFlatten(Iterator<? extends T> it, Mapper<T,? extends Iterator<U>> mapper) {
    	return new FlatMapperIterator<T,U>(mapper, it);
    }
    
    /**
     * Adds the elements from the specified iterator to the specified collection.
     * @param <T>
     * @param it
     * @param target
     * @return target
     */
    public static final <T> Collection<T> fill(Iterator<? extends T> it, Collection<T> target) {
        while (it.hasNext())
            target.add(it.next());
        return target;
    }
    
    /**
     * Upcasts an iterator.
     * @param <T>
     * @param <U>
     * @param it
     */
    @SuppressWarnings("unchecked")
	public static final <T,U extends T> Iterator<T> upcast(Iterator<U> it) {
    	return (Iterator<T>) it;
    }
	
	public static final class Item<T> {
		public final int index;
		public final T value;
		
		private Item(int index, T value) {
			super();
			this.index = index;
			this.value = value;
		}
	}
  	
	/**
	 * Filter and map composition.
	 * @param filter
	 * @param mapper
	 * @param it
	 */
	public static <T,U> Iterator<T> filterAndMap(Filter<U> filter, Mapper<U,T> mapper, Iterator<U> it) {
		return Mappers.apply(mapper, Filters.apply(filter, it));
	}
	
	public static <T,U,P> Iterator<T> filterAndMap(Filter<U> filter, ParamMapper<U,T,P> mapper, P param, Iterator<U> it) {
		return Mappers.apply(mapper, param, Filters.apply(filter, it));
	}
	
	/**
	 * Map and filter composition.
	 * @param mapper
	 * @param filter
	 * @param it
	 */
	public static <T,U> Iterator<T> mapAndFilter(Mapper<U,T> mapper, Filter<T> filter, Iterator<U> it) {
		return Filters.apply(filter, Mappers.apply(mapper, it));
	}
	
	/**
	 * Counts the remaining number of elements in the specified iterator.
	 * This method depletes the iterator.
	 * @param it
	 * @return the number of elements that remained in the specified iterator.
	 */
	public static int count(Iterator<?> it) {
		int result = 0;
		while (it.hasNext()) {
			result++;
			it.next();
		}
		return result;
	}
	
	private static final class EnumerationDecorator<T> implements Enumeration<T> {
		private final Iterator<T> iterator;

		private EnumerationDecorator(Iterator<T> iterator) {
			super();
			this.iterator = iterator;
		}

		@Override
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		@Override
		public T nextElement() {
			return iterator.next();
		}
	}
	
	public static final <T> Enumeration<T> getEnumeration(Iterator<T> it) {
		return new EnumerationDecorator<T>(it);
	}
	
	public static final <T> void removeAll(Iterator<T> it) {
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
	
	public static final <T> Iterator<T> flatten(Collection<Iterator<T>> iterators) {
		return flatten(iterators.iterator());
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> Iterator<T> flatten(Iterator<T>... iterators) {
		return flatten(Arrays.asList(iterators));
	}
}
