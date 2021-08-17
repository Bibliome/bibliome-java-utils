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

package fr.inra.maiage.bibliome.util.taxonomy.reject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.inra.maiage.bibliome.util.taxonomy.Name;

public class RejectDisjunction implements RejectName {
	private final Collection<RejectName> rejects;
	
	public RejectDisjunction() {
		this(new ArrayList<RejectName>());
	}
	
	public RejectDisjunction(Collection<RejectName> rejects) {
		super();
		this.rejects = rejects;
	}

	@Override
	public boolean reject(String taxid, Name name) {
		for (RejectName reject : rejects)
			if (reject.reject(taxid, name))
				return true;
		return false;
	}
	
	public void add(RejectName reject) {
		rejects.add(reject);
	}
	
	public void add(Collection<RejectName> rejects) {
		this.rejects.addAll(rejects);
	}

	@Override
	public RejectName simplify() {
		List<RejectName> rejects = new ArrayList<RejectName>(this.rejects.size());
		for (RejectName rn : this.rejects) {
			RejectName srn = rn.simplify();
			if (!srn.equals(RejectNone.INSTANCE)) {
				rejects.add(srn);
			}
		}
		if (rejects.isEmpty()) {
			return RejectNone.INSTANCE;
		}
		if (rejects.size() == 1) {
			return rejects.get(0);
		}
		return new RejectDisjunction(rejects);
	}
}
