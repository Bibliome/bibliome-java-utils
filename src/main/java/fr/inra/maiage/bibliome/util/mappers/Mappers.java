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

package fr.inra.maiage.bibliome.util.mappers;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Miscellaneous mapper utilities.
 * @author rbossy
 *
 */
public abstract class Mappers {
    private static class MappedIterator<T,U> implements Iterator<U> {
        private final Mapper<T,U> mapper;
        private final Iterator<? extends T> matrix;
        
        private MappedIterator(Mapper<T,U> mapper, Iterator<? extends T> matrix) {
            super();
            this.mapper = mapper;
            this.matrix = matrix;
        }

        @Override
        public boolean hasNext() {
            return matrix.hasNext();
        }

        @Override
        public U next() {
            return mapper.map(matrix.next());
        }

        @Override
        public void remove() {
            matrix.remove();
        }
    }
    
    private static final class DeParamMapper<S,T,P> implements Mapper<S,T> {
    	private final ParamMapper<S,T,P> paramMapper;
    	private final P param;
		
    	private DeParamMapper(ParamMapper<S,T,P> paramMapper, P param) {
			super();
			this.paramMapper = paramMapper;
			this.param = param;
		}

		@Override
		public T map(S x) {
			return paramMapper.map(x, param);
		}
    }
    
    public static final <S,T,P> Mapper<S,T> deParam(ParamMapper<S,T,P> paramMapper, P param) {
    	return new DeParamMapper<S,T,P>(paramMapper, param);
    }
    
    /**
     * Applies a mapper to each element of the specified iterator.
     * @param <T>
     * @param <U>
     * @param mapper
     * @param it
     * @return an iterator over the mapping of each value of the specified iterator
     */
    public static final <T,U> Iterator<U> apply(Mapper<T,U> mapper, Iterator<? extends T> it) {
        return new MappedIterator<T,U>(mapper, it);
    }
    
    public static final <T,U,P> Iterator<U> apply(ParamMapper<T,U,P> paramMapper, P param, Iterator<? extends T> it) {
    	return apply(deParam(paramMapper, param), it);
    }
    
    /**
     * Applies a mapper to each element in the specified collection and adds the mapped value to the target collection.
     * @param <T>
     * @param <U>
     * @param mapper
     * @param source
     * @param target
     * @return the target collection
     */
    public static final <T,U,C extends Collection<? super U>> C apply(Mapper<T,U> mapper, Collection<T> source, C target) {
        for (T x : source)
            target.add(mapper.map(x));
        return target;
    }

    public static final <T,U,P,C extends Collection<? super U>> C apply(ParamMapper<T,? extends U,P> paramMapper, P param, Collection<T> source, C target) {
    	return apply(deParam(paramMapper, param), source, target);
    }
    
    private static final class MappedCollection<T,U> extends AbstractCollection<U> {
        private final Mapper<T,U> mapper;
        private final Collection<T> source;
        
        public MappedCollection(Mapper<T,U> mapper, Collection<T> source) {
            super();
            this.mapper = mapper;
            this.source = source;
        }
        
        @Override
        public Iterator<U> iterator() {
            return apply(mapper, source.iterator());
        }

        @Override
        public int size() {
            return source.size();
        }
    }

    /**
     * Returns a collection containing the mapped elements from the specified collection.
     * The returned collection is a view of the transformed collection.
     * @param <T>
     * @param <U>
     * @param mapper
     * @param source
     * @return a collection containing the mapped elements from the specified collection
     */
    public static final <T,U> Collection<U> mappedCollection(Mapper<T,U> mapper, Collection<T> source) {
        return new MappedCollection<T,U>(mapper, source);
    }
    
    public static final <T,U,P> Collection<U> mappedCollection(ParamMapper<T,U,P> paramMapper, P param, Collection<T> source) {
    	return mappedCollection(deParam(paramMapper, param), source);
    }
    
    private static final class MappedList<T,U> extends AbstractList<U> {
        private final Mapper<T,U> mapper;
        private final List<T> source;
        
        private MappedList(Mapper<T,U> mapper, List<T> source) {
            super();
            this.mapper = mapper;
            this.source = source;
        }

        @Override
        public U get(int index) {
            return mapper.map(source.get(index));
        }

        @Override
        public int size() {
            return source.size();
        }
        
        @Override
        public Iterator<U> iterator() {
            return apply(mapper, source.iterator());
        }
    }
    
    /**
     * Returns a list containing the mapped elements from the specified list.
     * The returned list is a view of the transformed collection.
     * @param <T>
     * @param <U>
     * @param mapper
     * @param source
     * @return a list containing the mapped elements from the specified list
     */
    public static final <T,U> List<U> mappedList(Mapper<T,U> mapper, List<T> source) {
        return new MappedList<T,U>(mapper, source);
    }

    public static final <T,U,P> List<U> mappedList(ParamMapper<T,U,P> paramMapper, P param, List<T> source) {
    	return mappedList(deParam(paramMapper, param), source);
    }
    
    public static final <T,U> U fold(ParamMapper<T,U,U> paramMapper, U param, Iterator<T> it) {
    	while (it.hasNext())
    		param = paramMapper.map(it.next(), param);
    	return param;
    }
    
    public static final <T,U> U fold(ParamMapper<T,U,U> paramMapper, U param, Collection<T> c) {
    	return fold(paramMapper, param, c.iterator());
    }
    
    public static final <K,T,U,M extends Map<K,U>> M apply(Mapper<T,U> mapper, Map<K,T> source, M target) {
    	for (Map.Entry<K,T> e : source.entrySet())
    		target.put(e.getKey(), mapper.map(e.getValue()));
    	return target;
    }
    
    public static final <K,T,U,M extends Map<K,U>> M apply(ParamMapper<T,U,K> paramMapper, Map<K,T> source, M target) {
    	for (Map.Entry<K,T> e : source.entrySet()) {
    		K key = e.getKey();
    		target.put(key, paramMapper.map(e.getValue(), key));
    	}
    	return target;
    }

    public static final <K,V> V getDefault(Map<K,V> map, Mapper<K,V> mapper, K key, boolean keepDefaultValue) {
		if (map.containsKey(key))
			return map.get(key);
		V result = mapper.map(key);
		if (keepDefaultValue)
			map.put(key, result);
		return result;
	}
	
	public static <K,V> V getDefault(Map<K,V> map, Mapper<K,V> mapper, K key) {
		return getDefault(map, mapper, key, true);
	}
}
