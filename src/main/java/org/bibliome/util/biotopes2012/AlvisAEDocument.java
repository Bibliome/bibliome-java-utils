package org.bibliome.util.biotopes2012;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class AlvisAEDocument {
	private final AlvisAECampaign campaign;
	private final int id;
	private final String description;
	private final String contents;
	private final AlvisAEProperties properties = new AlvisAEProperties();
	private final Collection<AlvisAEAnnotationSet> annotationSets = new ArrayList<AlvisAEAnnotationSet>();
	
	public AlvisAEDocument(AlvisAECampaign campaign, int id, String description, String contents) {
		super();
		this.campaign = campaign;
		this.id = id;
		this.description = description;
		this.contents = contents;
		campaign.addDocument(this);
	}
	
	void addAnnotationSet(AlvisAEAnnotationSet annotationSet) {
		annotationSets.add(annotationSet);
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Collection<AlvisAEAnnotationSet> getAnnotationSets() {
		return Collections.unmodifiableCollection(annotationSets);
	}
	
	public AlvisAEAnnotation getAnnotationByID(int annotationSetID, String annotationID) {
		for (AlvisAEAnnotationSet aset : annotationSets)
			if (annotationSetID == aset.getId())
				return aset.getAnnotationByID(annotationID);
		return null;
	}

	public String getContents() {
		return contents;
	}

	public AlvisAEProperties getProperties() {
		return properties;
	}

	public AlvisAECampaign getCampaign() {
		return campaign;
	}
}
