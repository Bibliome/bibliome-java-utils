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

package org.bibliome.util.notation;

import java.util.Deque;
import java.util.LinkedList;

public abstract class AbstractListBuilderNotationHandler<T> implements NotationHandler {
	private final Deque<T> stack = new LinkedList<T>();
	private final T top;

	protected AbstractListBuilderNotationHandler() {
		super();
		top = createList();
		stack.push(top);
	}
	
	protected abstract T createList();
	
	protected abstract T createList(T parent);

	public T getTop() {
		return top;
	}

	@Override
	public void closeList(NotationParser parser) {
		stack.pop();
	}

	@Override
	public void openUnmappedList(NotationParser parser) {
		T current = stack.peek();
		T list = createList(current);
		addList(current, list);
		stack.push(list);
	}
	
	protected abstract void addList(T list, T value);

	@Override
	public void openMappedList(NotationParser parser, String key) {
		T current = stack.peek();
		T list = createList(current);
		addMap(current, key, list);
		stack.push(list);
	}
	
	protected abstract void addMap(T list, String key, T value);

	@Override
	public void addStringValue(NotationParser parser, String value) {
		T current = stack.peek();
		addString(current, value);
	}

	@Override
	public void directive(NotationParser parser, String directive) throws NotationParseException {
		throw parser.unknownDirective(directive);
	}
	
	protected abstract void addString(T list, String value);
}
