package org.bibliome.util.taxonomy.dict;

import java.io.IOException;
import java.util.logging.Logger;

import org.bibliome.util.taxonomy.Name;
import org.bibliome.util.taxonomy.Taxon;

enum TaxonNamePatterns implements TaxonNamePattern {
	NAME(true) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			target.append(name.name);
		}
	},
	
	NAME_TYPE(true) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			target.append(name.type);
		}
	},
	
	TAXID(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			target.append(Integer.toString(taxon.getTaxid()));
		}
	},
	
	RANK(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			target.append(taxon.getRank());
		}
	},

	CANONICAL(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			String canonical = taxon.getCanonicalName();
			if (canonical == null)
				logger.warning("no canonical name for taxon: " + taxon.getTaxid());
			else
				target.append(canonical);
		}
	},
	
	TAXID_PATH(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			for (Taxon ancestor : taxon.getPath(true, true)) {
				target.append(pathSeparator);
				target.append(Integer.toString(ancestor.getTaxid()));
			}
		}
	},
	
	CANONICAL_PATH(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			for (Taxon ancestor : taxon.getPath(true, true)) {
				target.append(pathSeparator);
				String canonical = ancestor.getCanonicalName();
				if (canonical == null)
					logger.warning("no canonical name for ancestor taxon: " + ancestor.getTaxid());
				else
					target.append(canonical);
			}
		}
	},
	
	PARENT_TAXID(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			Taxon parent = taxon.getParent();
			if (parent != null)
				target.append(Integer.toString(parent.getTaxid()));
		}
	},
	
	PARENT_CANONICAL(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			Taxon parent = taxon.getParent();
			if (parent == null)
				return;
			String canonical = parent.getCanonicalName();
			if (canonical == null)
				logger.warning("no canonical name for parent: " + parent.getTaxid());
			else
				target.append(canonical);
		}
	},
	
	PARENT_TAXID_PATH(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			for (Taxon ancestor : taxon.getPath(false, true)) {
				target.append(pathSeparator);
				target.append(Integer.toString(ancestor.getTaxid()));
			}
		}
	},

	PARENT_CANONICAL_PATH(false) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			for (Taxon ancestor : taxon.getPath(false, true)) {
				target.append(pathSeparator);
				String canonical = ancestor.getCanonicalName();
				if (canonical == null)
					logger.warning("no canonical name for ancestor taxon: " + ancestor.getTaxid());
				else
					target.append(canonical);
			}
		}
	},
	
	POS_TAG(true) {
		@Override
		public void appendValue(Logger logger, Taxon taxon, Name name, String pathSeparator, Appendable target) throws IOException {
			if ("blast name".equals(name.type))
				target.append("NNS");
			else if ("in-part".equals(name.type))
				target.append("NP"); // could be NNS
			else if ("misnomer".equals(name.type))
				target.append("NP"); // could be NN
			else if ("includes".equals(name.type))
				target.append("NP"); // has mixed names
			else if ("misspelling".equals(name.type))
				target.append("NP"); // has mixed names
			else if ("common name".equals(name.type))
				target.append("NN"); // has mixed names
			else if ("genbank common name".equals(name.type))
				target.append("NN"); // has mixed names
			else
				target.append("NP");
		}
	};
	
	private final boolean nameRequired;

	private TaxonNamePatterns(boolean nameRequired) {
		this.nameRequired = nameRequired;
	}

	@Override
	public boolean isNameRequired() {
		return nameRequired;
	}
}
