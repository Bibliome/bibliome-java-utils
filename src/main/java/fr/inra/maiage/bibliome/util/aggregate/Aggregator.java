package fr.inra.maiage.bibliome.util.aggregate;

public interface Aggregator {
	void add(String value);
	String get();
}
