package org.bibliome.util.tomap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.Strings;
import org.bibliome.util.mappers.Mappers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Candidate {
	private final List<Token> tokens;
	private Candidate head;
	private final Collection<Candidate> modifiers = new ArrayList<Candidate>();
	private Token headToken;
	
	public Candidate(List<Token> tokens) {
		super();
		this.tokens = tokens;
	}

	public Candidate() {
		this(new ArrayList<Token>());
	}

	public List<Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

	public Candidate getHead() {
		return head;
	}

	public Collection<Candidate> getModifiers() {
		return Collections.unmodifiableCollection(modifiers);
	}

	public Token getHeadToken() {
		return headToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Candidate other = (Candidate) obj;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		}
		else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	public void setHead(Candidate head) {
		this.head = head;
	}

	public void addModifier(Candidate mod) {
		this.modifiers.add(mod);
	}

	public void setHeadToken(Token headToken) {
		this.headToken = headToken;
	}
	
	public String getString(TokenToStringMapper tokenMapper) {
		List<String> tokenForms = Mappers.apply(tokenMapper, tokens, new ArrayList<String>(tokens.size()));
		return Strings.join(tokenForms, ' ');
	}
	
	public String getForm() {
		return getString(TokenToStringMapper.FORM);
	}
	
	public String getLemma() {
		return getString(TokenToStringMapper.LEMMA);
	}
	
	public String getPOS() {
		return getString(TokenToStringMapper.POS);
	}
	
	private Element toDOM(Document doc, String tag, List<String> conceptIDs) {
		Element result = doc.createElement(tag);
		for (Token t : tokens) {
			Element te = t.toDOM(doc, "t");
			result.appendChild(te);
		}
		Element hte = headToken.toDOM(doc, "ht");
		result.appendChild(hte);
		if (head != null) {
			Element he = head.toDOM(doc, "head");
			result.appendChild(he);
		}
		for (Candidate mod : modifiers) {
			Element me = mod.toDOM(doc, "mod");
			result.appendChild(me);
		}
		for (String cid : conceptIDs) {
			Element ce = doc.createElement("concept");
			ce.setTextContent(cid);
			result.appendChild(ce);
		}
		return result;
	}
	
	private Element toDOM(Document doc, String tag) {
		return toDOM(doc, tag, Collections.<String> emptyList());
	}
	
	public Element toDOM(Document doc, List<String> conceptIDs) {
		return toDOM(doc, "candidate", conceptIDs);
	}
	
	public Element toDOM(Document doc) {
		return toDOM(doc, "candidate");
	}
	
	private void toString(StringBuilder sb, String prefix) {
		sb.append(' ');
		sb.append(prefix);
		sb.append('(');
		toString(sb);
		sb.append(')');
	}
	
	public void toString(StringBuilder sb) {
		for (Token t : tokens) {
			sb.append(t);
			sb.append(' ');
		}
		sb.append(':');
		if (head != null) {
			head.toString(sb, "H");
		}
		for (Candidate mod : modifiers) {
			mod.toString(sb, "M");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
}
