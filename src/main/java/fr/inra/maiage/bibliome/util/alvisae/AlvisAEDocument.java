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

package fr.inra.maiage.bibliome.util.alvisae;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.util.mappers.Mapper;

public class AlvisAEDocument { 
	static final String TABLE = "document";
	static final String FIELD_ID = "id";
	static final String FIELD_DESCRIPTION = "description";
	static final String FIELD_EXTERNAL_ID = "external_id";
	static final String FIELD_HTML = "html_annset";
	static final String[] FIELDS = new String[] {
		FIELD_ID,
		FIELD_DESCRIPTION,
		FIELD_EXTERNAL_ID,
		FIELD_HTML
	};
	static final String[] FIELDS_OLD_MODEL = new String[] {
		FIELD_ID,
		FIELD_DESCRIPTION,
		FIELD_HTML
	};
	static final String FIELD_CONTENTS = "contents";

	private final Campaign campaign;
	private final int id;
	private final String description;
	private final String contents;
	private final String externalId;
	private final Collection<AnnotationSet> annotationSets = new ArrayList<AnnotationSet>();
	private final AnnotationSet htmlAnnotationSet;
	
	public AlvisAEDocument(Campaign campaign, int id, String description, String contents, String externalId) {
		super();
		this.campaign = campaign;
		this.id = id;
		this.description = description;
		this.contents = contents;
		this.externalId = externalId;
		campaign.addDocument(this);
		this.htmlAnnotationSet = new AnnotationSet(campaign, id, 0, 0, false, 0, null, 0, null, true, null, null);
	}
	
	AlvisAEDocument(Campaign campaign, ResultSet rs, boolean loadContents) throws SQLException, ParseException {
		this(
				campaign,
				rs.getInt(FIELD_ID),
				rs.getString(FIELD_DESCRIPTION),
				(loadContents ? rs.getString(FIELD_CONTENTS) : null),
				campaign.isOldModel() ? rs.getString(FIELD_ID) : rs.getString(FIELD_EXTERNAL_ID)
				);
		htmlAnnotationSet.parseTextBounds(rs.getString(FIELD_HTML));
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getContents() {
		return contents;
	}

	public String getExternalId() {
		return externalId;
	}
	
	void addAnnotationSet(AnnotationSet set) {
		annotationSets.add(set);
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public Collection<AnnotationSet> getAnnotationSets() {
		return Collections.unmodifiableCollection(annotationSets);
	}
	
	public Collection<TextBound> getHTML() {
		return htmlAnnotationSet.getTextBounds();
	}
	
	public SourceAnnotation resolveSourceAnnotation(SourceAnnotationReference ref) {
		AnnotationSet aSet = resolveAnnotationSet(ref.getAnnotationSetId());
		if (aSet == null) {
			throw new RuntimeException("annotation set '"+ref.getAnnotationSetId()+"' was not loaded");
		}
		AlvisAEAnnotation annotation = aSet.resolveAnnotation(ref.getAnnotationId());
		return new SourceAnnotation(annotation, ref.getStatus());
	}

	private AnnotationSet resolveAnnotationSet(int annotationSetId) {
		for (AnnotationSet aSet : annotationSets) {
			if (aSet.getId() == annotationSetId) {
				return aSet;
			}
		}
		return null;
	}
	
	public final Mapper<SourceAnnotationReference,SourceAnnotation> SOURCE_ANNOTATION_RESOLVER = new Mapper<SourceAnnotationReference,SourceAnnotation>() {
		@Override
		public SourceAnnotation map(SourceAnnotationReference x) {
			return resolveSourceAnnotation(x);
		}
	};
}
