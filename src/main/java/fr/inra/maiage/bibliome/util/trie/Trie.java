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

package fr.inra.maiage.bibliome.util.trie;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.util.marshall.AbstractMarshaller;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

/**
 * A trie contains a set of key/value pairs.
 * Keys are character sequences and values are of type T.
 * @author rbossy
 *
 * @param <T>
 */
public class Trie<T> implements Closeable {
	private final State<T> root;
	private final Unmarshaller<State<T>> stateUnmarshaller;
	private final Unmarshaller<List<T>> valuesUnmarshaller;
	
	/**
	 * Creates an empty trie.
	 */
	public Trie() {
		root = new State<T>();
		stateUnmarshaller = null;
		valuesUnmarshaller = null;
	}
	
	/**
	 * Loads a trie saved with save().
	 * It is the caller responsibility to set the channel at the same position it was when save() have been called.
	 * @param channel
	 * @param valueDecoder
	 * @throws IOException
	 */
	public Trie(FileChannel channel, Decoder<T> valueDecoder) throws IOException {
		stateUnmarshaller = new Unmarshaller<State<T>>(channel, new StateDecoder<T>(this));
		root = stateUnmarshaller.read(AbstractMarshaller.getPosition(channel));
		Unmarshaller<T> valueUnmarshaller = new Unmarshaller<T>(channel, valueDecoder);
		valuesUnmarshaller = new Unmarshaller<List<T>>(channel, new ValuesDecoder<T>(valueUnmarshaller));
	}

	/**
	 * Loads a trie saved with save().
	 * @param path
	 * @param valueDecoder
	 * @throws IOException
	 */
	public Trie(Path path, Decoder<T> valueDecoder) throws IOException {
		this(FileChannel.open(path, StandardOpenOption.READ), valueDecoder);
	}
	
	/**
	 * Loads a trie saved with save().
	 * @param path
	 * @param valueDecoder
	 * @throws IOException
	 */
	public Trie(String path, Decoder<T> valueDecoder) throws IOException {
		this(Paths.get(path), valueDecoder);
	}

	/**
	 * Loads a trie saved with save().
	 * @param file
	 * @param valueDecoder
	 * @throws IOException
	 */
	public Trie(File file, Decoder<T> valueDecoder) throws IOException {
		this(file.toPath(), valueDecoder);
	}
	
	private long save(FileChannel channel, Encoder<T> valueEncoder, boolean close) throws IOException {
		StateEncoder<T> stateEncoder = new StateEncoder<T>();
		Marshaller<State<T>> stateMarshaller = new Marshaller<State<T>>(channel, stateEncoder);
		Marshaller<T> valueMarshaller = new Marshaller<T>(channel, valueEncoder);
		Encoder<List<T>> valuesEncoder = new ValuesEncoder<T>(valueMarshaller);
		Marshaller<List<T>> valuesMarshaller = new Marshaller<List<T>>(channel, valuesEncoder);
		stateEncoder.setMarshaller(stateMarshaller);
		stateEncoder.setValuesMarshaller(valuesMarshaller);
		long result = stateMarshaller.write(root);
		if (close)
			channel.close();
		return result;
	}
	
	/**
	 * Saves this trie in the specified channel.
	 * @param channel
	 * @param valueEncoder
	 * @return the position of the trie in the channel.
	 * @throws IOException
	 */
	public long save(FileChannel channel, Encoder<T> valueEncoder) throws IOException {
		return save(channel, valueEncoder, false);
	}

	/**
	 * Saves this trie in a file at the specified path.
	 * @param path
	 * @param valueEncoder
	 * @return the position of the trie in the file.
	 * @throws IOException
	 */
	public long save(Path path, Encoder<T> valueEncoder) throws IOException {
		return save(FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE), valueEncoder, true);
	}
	
	/**
	 * Saves this trie in a file at the specified path.
	 * @param path
	 * @param valueEncoder
	 * @return the position of the trie in the file.
	 * @throws IOException
	 */
	public long save(String path, Encoder<T> valueEncoder) throws IOException {
		return save(Paths.get(path), valueEncoder);
	}
	
	/**
	 * Saves this trie in the specified file.
	 * @param file
	 * @param valueEncoder
	 * @return the position of the trie in the file.
	 * @throws IOException
	 */
	public long save(File file, Encoder<T> valueEncoder) throws IOException {
		return save(file.toPath(), valueEncoder);
	}
	
	Unmarshaller<State<T>> getStateUnmarshaller() {
		return stateUnmarshaller;
	}

	Unmarshaller<List<T>> getValuesUnmarshaller() {
		return valuesUnmarshaller;
	}

	State<T> getRoot() {
		return root;
	}
	
	private State<T> addKey(CharSequence key) {
		State<T> result = root;
		for (int i = 0; i < key.length(); ++i)
			result = result.extend(key.charAt(i));
		return result;
	}
	
	/**
	 * Adds a key/value pair in this trie.
	 * @param key
	 * @param value
	 * @return true iff the specified key did not have a value before this method was called.
	 */
	public boolean addEntry(CharSequence key, T value) {
		State<T> state = addKey(key);
		return state.addValue(value);
	}
	
	/**
	 * Adds entries that share the same key.
	 * @param key
	 * @param values
	 * @return true if the key was not present in the trie before the specified key was added.
	 */
	public boolean addEntries(CharSequence key, Collection<? extends T> values) {
		State<T> state = addKey(key);
		return state.addValues(values);
	}
	
	/**
	 * Add entries from the specified map.
	 * @param map
	 * @return true if  at least one key was not present in the trie before the specified map was added.
	 */
	public boolean addEntries(Map<? extends CharSequence,? extends T> map) {
		boolean result = false;
		for (Map.Entry<? extends CharSequence,? extends T> e : map.entrySet())
			result = addEntry(e.getKey(), e.getValue()) || result;
		return result;
	}
	
	/**
	 * Add entries from the specified map.
	 * @param map
	 * @return true if  at least one key was not present in the trie before the specified map was added.
	 */
	public boolean addAllEntries(Map<? extends CharSequence,? extends Collection<? extends T>> map) {
		boolean result = false;
		for (Map.Entry<? extends CharSequence,? extends Collection<? extends T>> e : map.entrySet())
			result = addEntries(e.getKey(), e.getValue()) || result;
		return result;		
	}
	
	/**
	 * Returns the values associated to the specified key, null if this trie has not the specified key.
	 * @param key
	 */
	public List<T> getValues(CharSequence key) {
		State<T> state = root;
		for (int i = 0; i < key.length(); ++i) {
			state = state.getState(key.charAt(i));
			if (state == null)
				return null;
		}
		return state.getValues();
	}

	@Override
	public void close() throws IOException {
		if (stateUnmarshaller != null)
			stateUnmarshaller.close();
		if (valuesUnmarshaller != null)
			valuesUnmarshaller.close();
	}

	/**
	 * Prints the state tree to the specified stream.
	 * @param ps
	 */
	public void tree(PrintStream ps) {
		root.tree(ps, 0);
	}
}
