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

package org.bibliome.util.biotopes2012;

public class DownCastAnnotation {
	private static final AlvisAEAnnotationVisitor<TextBound,Void> TEXT_BOUND = new AlvisAEAnnotationVisitor<TextBound,Void>() {
		@Override
		public TextBound visit(TextBound textBound, Void param) {
			return textBound;
		}

		@Override
		public TextBound visit(Group group, Void param) {
			return null;
		}

		@Override
		public TextBound visit(Relation relation, Void param) {
			return null;
		}
	};
	
	private static final AlvisAEAnnotationVisitor<Group,Void> GROUP = new AlvisAEAnnotationVisitor<Group,Void>() {
		@Override
		public Group visit(TextBound textBound, Void param) {
			return null;
		}

		@Override
		public Group visit(Group group, Void param) {
			return group;
		}

		@Override
		public Group visit(Relation relation, Void param) {
			return null;
		}
	};
	
	private static final AlvisAEAnnotationVisitor<Relation,Void> RELATION = new AlvisAEAnnotationVisitor<Relation,Void>() {
		@Override
		public Relation visit(TextBound textBound, Void param) {
			return null;
		}

		@Override
		public Relation visit(Group group, Void param) {
			return null;
		}

		@Override
		public Relation visit(Relation relation, Void param) {
			return relation;
		}
	};
	
	public static TextBound toTextBound(AlvisAEAnnotation a) {
		return a.accept(TEXT_BOUND, null);
	}
	
	public static Group toGroup(AlvisAEAnnotation a) {
		return a.accept(GROUP, null);
	}
	
	public static Relation toRelation(AlvisAEAnnotation a) {
		return a.accept(RELATION, null);
	}
}
