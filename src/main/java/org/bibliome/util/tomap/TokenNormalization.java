package org.bibliome.util.tomap;

public enum TokenNormalization {
	FORM {
		@Override
		public void normalize(StringNormalization sn, Token token) {
			token.setNormalizationObject(sn.normalize(token.getForm()));
		}
	},
	
	LEMMA {
		@Override
		public void normalize(StringNormalization sn, Token token) {
			token.setNormalizationObject(sn.normalize(token.getLemma()));
		}
	},
	
	DEFAULT_LEMMA {
		private String getDefaultLemma(Token token) {
			String lemma = token.getLemma();
			if (lemma != null) {
				return lemma;
			}
			return token.getForm();
		}
		
		@Override
		public void normalize(StringNormalization sn, Token token) {
			String lemma = getDefaultLemma(token);
			token.setNormalizationObject(sn.normalize(lemma));
		}
	},
	
	STRICT {
		@Override
		public void normalize(StringNormalization sn, Token token) {
			String form = sn.normalize(token.getForm());
			String lemma = sn.normalizeNull(token.getLemma());
			String pos = sn.normalizeNull(token.getPos());
			token.setNormalizationObject(new Token(form, lemma, pos));
		}
	};
	
	public abstract void normalize(StringNormalization sn, Token token);
}
