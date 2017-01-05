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

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NotationParser implements NotationHandler {
	private final Reader in;
	private final String source;
	private int lineno;
	private int column;
	private ParserState parserState = ParserState_Indent.getInstance();
	private final Deque<StackElement> stack = new LinkedList<StackElement>();

	public NotationParser(Syntax syntax, Map<String,String> variables, NotationHandler handler, Reader in, String source) {
		super();
		this.in = in;
		this.source = source;
		stack.push(new StackElement(syntax, variables, handler));
	}

	public NotationParser(Map<String,String> variables, NotationHandler handler, Reader in, String source) {
		this(new Syntax(), variables, handler, in, source);
	}

	public NotationParser(Syntax syntax, NotationHandler handler, Reader in, String source) {
		this(syntax, new HashMap<String,String>(), handler, in, source);
	}

	public NotationParser(NotationHandler handler, Reader in, String source) {
		this(new Syntax(), new HashMap<String,String>(), handler, in, source);
	}

	void setParserState(ParserState parserState) {
		this.parserState = parserState;
	}

	public Syntax getSyntax() {
		return stack.peek().syntax;
	}
	
	public NotationHandler getHandler() {
		return stack.peek().handler;
	}
	
	public void setHandler(NotationHandler handler) {
		stack.peek().handler = handler;
	}
	
	public String getSource() {
		return source;
	}

	public int getLineno() {
		return lineno;
	}

	public int getColumn() {
		return column;
	}

	public void parse() throws IOException, NotationParseException {
		while (true) {
			int r = in.read();
			if (r == -1) {
				parserState.consumeEOF(this);
				return;
			}
			char c = (char) r;
			if (c == Syntax.getNl()) {
				lineno++;
				column = 0;
			}
			else {
				column++;
			}
			parserState.consumeCharacter(this, c);
		}
	}
	
	public String getMessage(String msg) {
		return String.format("%s line %d, column %d: %s", source, lineno, column, msg);
	}
	
	public NotationParseException parseError(String msg) {
		return new NotationParseException(getMessage(msg));
	}
	
	NotationParseException unknownDirective(String directive) {
		return new NotationParseException(getMessage("unknown directive " + directive));
	}
	
	NotationParseException unexpectedCharacter(char c) {
		return parseError(String.format("unexpected character '%c'", c));
	}
	
	NotationParseException wrongIndent(int expected, int indent) {
		return parseError(String.format("wrong indent, expected %d, got %d", expected, indent));
	}

	void checkIndent(int indent) throws NotationParseException {
		Integer topExpectedIndent = null;
		while (true) {
			StackElement stackElement = stack.peek();
			int expectedIndent = stackElement.expectedIndent;
			if (topExpectedIndent == null) {
				topExpectedIndent = expectedIndent;
			}
			if (indent == expectedIndent) {
				if (stackElement.expectMore) {
					closeList(this);
				}
				return;
			}
			if (indent > expectedIndent) {
				if (stackElement.expectMore) {
					stackElement.updateExpectedIndent(indent);
					return;
				}
				throw wrongIndent(topExpectedIndent, indent);
			}
			// indent < expectedIndent
			closeList(this);
		}
	}

	@Override
	public void closeList(NotationParser parser) throws NotationParseException {
		getHandler().closeList(parser);
		stack.pop();
	}
	
	void push() {
		StackElement current = stack.peek();
		StackElement elt = new StackElement(current);
		stack.push(elt);
	}

	@Override
	public void openUnmappedList(NotationParser parser) throws NotationParseException {
		push();
		getHandler().openUnmappedList(parser);
	}
	
	@Override
	public void openMappedList(NotationParser parser, String key) throws NotationParseException {
		push();
		getHandler().openMappedList(parser, key);
	}
	
	@Override
	public void directive(NotationParser parser, String directive) throws NotationParseException {
		getHandler().directive(parser, directive);
	}

	@Override
	public void addStringValue(NotationParser parser, String value) throws NotationParseException {
		getHandler().addStringValue(parser, value);
	}

	public Map<String,String> getVariables() {
		return stack.peek().variables;
	}
	
	public String getVariableValue(String name) throws NotationParseException {
		for (StackElement elt : stack) {
			Map<String,String> variables = elt.variables;
			if (variables.containsKey(name)) {
				return variables.get(name);
			}
		}
		throw parseError("undefined variable: " + name);
	}

	private static class StackElement {
		private final Syntax syntax;
		private final Map<String,String> variables;
		private NotationHandler handler;
		private int expectedIndent;
		private boolean expectMore;
		
		private StackElement(StackElement parent) {
			this.syntax = new Syntax(parent.syntax);
			this.variables = new HashMap<String,String>();
			this.handler = parent.handler;
			this.expectedIndent = parent.expectedIndent;
			this.expectMore = true;
		}
		
		private StackElement(Syntax syntax, Map<String,String> variables, NotationHandler handler) {
			if (syntax == null) {
				throw new NullPointerException();
			}
			this.syntax = syntax;
			if (variables == null) {
				throw new NullPointerException();
			}
			this.variables = variables;
			if (handler == null) {
				throw new NullPointerException();
			}
			this.handler = handler;
			this.expectedIndent = 0;
			this.expectMore = false;
		}

		private void updateExpectedIndent(int expectedIndent) {
			this.expectedIndent = expectedIndent;
			this.expectMore = false;
		}

		@Override
		public String toString() {
			return "StackElement " + System.identityHashCode(this) + " [expectedIndent=" + expectedIndent + ", expectMore=" + expectMore + "]";
		}
	}
}
