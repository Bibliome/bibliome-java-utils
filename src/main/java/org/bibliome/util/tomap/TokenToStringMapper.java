package org.bibliome.util.tomap;

import org.bibliome.util.mappers.Mapper;

public enum TokenToStringMapper implements Mapper<Token,String> {
	FORM {
		@Override
		public String map(Token x) {
			return x.getForm();
		}
	},
	
	LEMMA {
		@Override
		public String map(Token x) {
			return x.getLemma();
		}
	},
	
	POS {
		@Override
		public String map(Token x) {
			return x.getPos();
		}
	};
}
