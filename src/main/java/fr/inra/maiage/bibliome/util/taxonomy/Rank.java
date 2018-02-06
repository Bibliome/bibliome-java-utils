package fr.inra.maiage.bibliome.util.taxonomy;

public enum Rank {
	FORM("form"),
	VARIETAS("varietas"),
	SUBSPECIES("subspecies"),
	SPECIES_SUBGROUP("subspecies group"),
	SPECIES("species"),
	SPECIES_GROUP("species group"),
	SUBGENUS("subgenus"),
	GENUS("genus"),
	SUBTRIBE("subtribe"),
	TRIBE("tribe"),
	SUBFAMILY("subfamily"),
	FAMILY("family"),
	SUPERFAMILY("superfamily"),
	INFRAORDER("infraorder"),
	SUBORDER("suborder"),
	ORDER("order"),
	SUPERORDER("superorder"),
	PARVORDER("parvorder"),
	INFRACLASS("infraclass"),
	SUBCLASS("subclass"),
	CLASS("class"),
	SUPERCLASS("superclass"),
	SUBPHYLUM("subphylum"),
	PHYLUM("phylum"),
	SUPERPHYLUM("superphylum"),
	SUBKINGDOM("subkingdom"),
	KINGDOM("kingdom"),
	SUPERKINGDOM("superkingdom")
	;

	public final String name;

	private Rank(String name) {
		this.name = name;
	}
}
