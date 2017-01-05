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

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;

public class Test {
	public static void main(String[] args) throws IOException, NotationParseException {
		StandardBuilderNotationHandler handler = new StandardBuilderNotationHandler();
		Reader in = new FileReader(args[0]);
		NotationParser parser = new NotationParser(new DirectiveHandler(handler), in, args[0]);
		parser.parse();
		new PrintVisitor(parser.getSyntax(), System.out).visit(handler.getTop(), new PrintVisitorParameters("", true, false));
	}

	private static class PrintVisitorParameters {
		private final String depth;
		private final boolean hasKey;
		private final boolean keyString;
		
		private PrintVisitorParameters(String depth, boolean hasKey, boolean keyString) {
			super();
			this.depth = depth;
			this.hasKey = hasKey;
			this.keyString = keyString;
		}
		
		
	}
	
	private static class PrintVisitor extends StandardNotationVisitor<Void,PrintVisitorParameters> {
		private final Syntax syntax;
		private final PrintStream out;

		private PrintVisitor(Syntax syntax, PrintStream out) {
			super();
			this.syntax = syntax;
			this.out = out;
		}
		
		private void printMap() {
			out.print("\u001B[32m");
			out.print(syntax.getMap1());
			out.print("\u001B[0m");
		}
		
		private void printKey(String key) {
			out.print("\u001B[32m");
			out.print(syntax.printable(key, true));
			out.print("\u001B[0m");
		}

		@Override
		public Void visit(List<Object> list, PrintVisitorParameters param) {
			if (param.hasKey) {
				for (Object item : list) {
					visit(item, new PrintVisitorParameters(param.depth, false, false));
				}
				return null;
			}
			out.print(param.depth);
			printMap();
			out.println();
			param = new PrintVisitorParameters(param.depth + Syntax.getTab(), true, false);
			for (Object item : list) {
				visit(item, param);
			}
			return null;
		}

		@Override
		public Void visit(String value, PrintVisitorParameters param) {
			out.print(param.depth);
			out.print(syntax.printable(value, param.keyString));
			out.println();
			return null;
		}

		@Override
		public Void visit(String key, List<Object> list, PrintVisitorParameters param) {
			out.print(param.depth);
			printKey(key);
			printMap();
			String head = getHead(list);
			if (head != null) {
				out.print(' ');
				out.print(syntax.printable(head, param.keyString));
				out.println();
				param = new PrintVisitorParameters(param.depth + Syntax.getTab(), true, false);
				visit(list.subList(1, list.size()), param);
				return null;
			}
			out.println();
			param = new PrintVisitorParameters(param.depth + Syntax.getTab(), true, false);
			visit(list, param);
			return null;
		}
	}
}
