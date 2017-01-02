package org.bibliome.util.tomap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Token {
	private final String form;
	private final String lemma;
	private final String pos;
	private Object normalizationObject;
	
	public Token(String form, String lemma, String pos) {
		super();
		this.form = form;
		this.lemma = lemma;
		this.pos = pos;
	}

	public String getForm() {
		return form;
	}

	public String getLemma() {
		return lemma;
	}

	public String getPos() {
		return pos;
	}

	@Override
	public int hashCode() {
		if (normalizationObject != null) {
			return normalizationObject.hashCode();
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + ((form == null) ? 0 : form.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		if (normalizationObject != null) {
			return normalizationObject.equals(other.normalizationObject);
		}
		if (form == null) {
			if (other.form != null)
				return false;
		} else if (!form.equals(other.form))
			return false;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}
	
	void setNormalizationObject(Object obj) {
		normalizationObject = obj;
	}

	public Element toDOM(Document doc, String tag) {
		Element result = doc.createElement(tag);
		result.setAttribute("lemma", lemma);
		result.setAttribute("pos", pos);
		result.setTextContent(form);
		return result;
	}
	
	public void toString(StringBuilder sb) {
		sb.append(form);
		sb.append(':');
		sb.append(pos);
		sb.append(':');
		sb.append(lemma);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}
