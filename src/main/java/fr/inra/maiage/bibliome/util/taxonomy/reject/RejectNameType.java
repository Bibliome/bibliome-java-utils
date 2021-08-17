package fr.inra.maiage.bibliome.util.taxonomy.reject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import fr.inra.maiage.bibliome.util.taxonomy.Name;

public class RejectNameType implements RejectName {
	private final Collection<String> nameTypes = new HashSet<String>();
	
	public RejectNameType(String... nameTypes) {
		this(Arrays.asList(nameTypes));
	}
	
	public RejectNameType(Collection<String> nameTypes) {
		this.nameTypes.addAll(nameTypes);
	}

	@Override
	public boolean reject(String taxid, Name name) {
		return nameTypes.contains(name.type);
	}

	@Override
	public RejectName simplify() {
		if (nameTypes.isEmpty()) {
			return RejectNone.INSTANCE;
		}
		return this;
	}
}
