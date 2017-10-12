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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.util.count.Count;
import fr.inra.maiage.bibliome.util.count.CountStats;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.FragmentComparator;
import fr.inra.maiage.bibliome.util.mappers.Mapper;

public class AnnotationSet {
	static final String TABLE = "annotationset";
	static final String FIELD_DOCUMENT_REFERENCE = "doc_id";
	static final String FIELD_CAMPAIGN_REFEERNCE = "campaign_id";
	static final String FIELD_HEAD = "head";
	static final String FIELD_REVISION = "revision";
	static final String FIELD_USER_REFERENCE = "user_id";
	static final String FIELD_TASK_REFERENCE = "task_id";
	static final String FIELD_ID = "id";
	static final String FIELD_CREATED = "created";
	static final String FIELD_PUBLISHED = "published";
	static final String FIELD_TEXT_BOUNDS = "text_annotations";
	static final String FIELD_GROUPS = "groups";
	static final String FIELD_RELATIONS = "relations";
	static final String FIELD_UNMATCHED = "unmatched";
	static final String[] FIELDS = new String[] {
		FIELD_DOCUMENT_REFERENCE,
		TABLE + "." + FIELD_ID,
		FIELD_REVISION,
		FIELD_HEAD,
		FIELD_CREATED,
		FIELD_PUBLISHED,
		FIELD_TEXT_BOUNDS,
		FIELD_GROUPS,
		FIELD_RELATIONS,
		FIELD_UNMATCHED
	};
	static final String[] FIELDS_OLD_MODEL = new String[] {
		FIELD_DOCUMENT_REFERENCE,
		TABLE + "." + FIELD_ID,
		FIELD_REVISION,
		FIELD_HEAD,
		FIELD_CREATED,
		FIELD_TEXT_BOUNDS,
		FIELD_GROUPS,
		FIELD_RELATIONS
	};

	static final String TABLE_TASK = "taskdefinition";
	static final String FIELD_TASK_ID_ALIAS = "taskid";
	static final String FIELD_TASK_ID = "id";
	static final String FIELD_TASK_NAME = "name";
	static final String TABLE_USER = "\"user\"";
	static final String FIELD_USER_ID = "id";
	static final String FIELD_USER_ID_ALIAS = "userid";
	static final String FIELD_USER_NAME = "login";
	
	static final String TABLE_DEPENDENCY = "annotationsetdependency";
	static final String FIELD_DEPENDENCY_SELF = "referent_id";
	static final String FIELD_DEPENDENCY_SOURCE = "referred_id";
	

	private final Campaign campaign;
	private final int document;
	private final int id;
	private final int revision;
	private final boolean referent;
	private final int userId;
	private final String user;
	private final int taskId;
	private final String task;
	private final boolean head;
	private final String created;
	private final String published;
	private final Map<String,AlvisAEAnnotation> annotations = new HashMap<String,AlvisAEAnnotation>();
	private final Collection<TextBound> textBounds = new ArrayList<TextBound>();
	private final Collection<Group> groups = new ArrayList<Group>();
	private final Collection<Relation> relations = new ArrayList<Relation>();
	private final SourceAnnotationCollection unmatched = new SourceAnnotationCollection();
	private final Collection<AnnotationSet> dependencies = new LinkedHashSet<AnnotationSet>();

	public AnnotationSet(Campaign campaign, int document, int id, int revision, boolean referent, int userId, String user, int taskId, String task, boolean head, String created, String published) {
		super();
		this.campaign = campaign;
		this.document = document;
		this.id = id;
		this.revision = revision;
		this.referent = referent;
		this.userId = userId;
		this.user = user;
		this.taskId = taskId;
		this.task = task;
		this.head = head;
		this.created = created;
		this.published = published;
		campaign.resolveDocument(document).addAnnotationSet(this);
	}
	
	AnnotationSet(Campaign campaign, ResultSet rs, boolean referent) throws SQLException {
		this(
				campaign,
				rs.getInt(FIELD_DOCUMENT_REFERENCE),
				rs.getInt(FIELD_ID),
				rs.getInt(FIELD_REVISION),
				referent,
				rs.getInt(FIELD_USER_ID_ALIAS),
				rs.getString(FIELD_USER_NAME),
				campaign.isOldModel() ? 0 : rs.getInt(FIELD_TASK_ID_ALIAS),
				campaign.isOldModel() ? "annotation" : rs.getString(FIELD_TASK_NAME),
				rs.getBoolean(FIELD_HEAD),
				rs.getString(FIELD_CREATED),
				campaign.isOldModel() ? rs.getString(FIELD_CREATED) : rs.getString(FIELD_PUBLISHED)
				);
	}
	
	void load(LoadOptions options, ResultSet rs) throws ParseException, SQLException {
		if (options.isLoadTextBound())
			loadTextBounds(rs);
		if (options.isLoadGroups())
			loadGroups(rs);
		if (options.isLoadRelations())
			loadRelations(rs);
		if (options.isLoadUnmatched())
			loadUnmatched(rs);
	}
	
	private void loadTextBounds(ResultSet rs) throws ParseException, SQLException {
		parseTextBounds(rs.getString(FIELD_TEXT_BOUNDS));
	}
	
	private void loadGroups(ResultSet rs) throws ParseException, SQLException {
		parseGroups(rs.getString(FIELD_GROUPS));
	}
	
	private void loadRelations(ResultSet rs) throws ParseException, SQLException {
		parseRelations(rs.getString(FIELD_RELATIONS));
	}
	
	private void loadUnmatched(ResultSet rs) throws ParseException, SQLException {
		parseUnmatched(rs.getString(FIELD_UNMATCHED));
	}

	void loadTextBounds(JSONArray jTexts) {
		for (Object o : jTexts)
			new TextBound(this, (JSONObject) o);
	}
	
	void parseTextBounds(String sValue) throws ParseException {
		JSONParser parser = new JSONParser();
		loadTextBounds((JSONArray) parser.parse(sValue));
	}
	
	void loadUnmatched(JSONArray sources) {
		unmatched.load(sources);
	}
	
	void parseUnmatched(String sValue) throws ParseException {
		if (sValue != null) {
			JSONParser parser = new JSONParser();
			loadUnmatched((JSONArray) parser.parse(sValue));
		}
	}
	
	void loadGroups(JSONArray jGroups) {
		for (Object o : jGroups)
			new Group(this, (JSONObject) o);
	}
	
	void parseGroups(String sValue) throws ParseException {
		JSONParser parser = new JSONParser();
		loadGroups((JSONArray) parser.parse(sValue));
	}
	
	void loadRelations(JSONArray jRelations) {
		for (Object o : jRelations)
			new Relation(this, (JSONObject) o);
	}
	
	void parseRelations(String sValue) throws ParseException {
		JSONParser parser = new JSONParser();
		loadRelations((JSONArray) parser.parse(sValue));
	}

	public boolean hasAnnotation(String id) {
		return annotations.containsKey(id);
	}
	
	public AlvisAEAnnotation resolveAnnotation(String id) {
		if (annotations.containsKey(id))
			return annotations.get(id);
		throw new RuntimeException();
	}
	
	public final Mapper<String,AlvisAEAnnotation> ANNOTATION_RESOLVER = new Mapper<String,AlvisAEAnnotation>() {
		@Override
		public AlvisAEAnnotation map(String x) {
			return resolveAnnotation(x);
		}
	};

	private void addAnnotation(AlvisAEAnnotation ann) {
		if (annotations.containsKey(ann.getId()))
			throw new RuntimeException();
		annotations.put(ann.getId(), ann);
	}
	
	void addTextBound(TextBound textBound) {
		addAnnotation(textBound);
		textBounds.add(textBound);
	}
	
	void addGroup(Group group) {
		addAnnotation(group);
		groups.add(group);
	}
	
	void addRelation(Relation rel) {
		addAnnotation(rel);
		relations.add(rel);
	}
	
	void addDependency(AnnotationSet annotationSet) {
		dependencies.add(annotationSet);
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public AlvisAEDocument getDocument() {
		return getCampaign().resolveDocument(document);
	}

	public int getId() {
		return id;
	}

	public int getRevision() {
		return revision;
	}

	public int getUserId() {
		return userId;
	}

	public String getUser() {
		return user;
	}

	public int getTaskId() {
		return taskId;
	}

	public String getTask() {
		return task;
	}

	public boolean isHead() {
		return head;
	}

	public String getCreated() {
		return created;
	}

	public String getPublished() {
		return published;
	}
	
	public boolean isReferent() {
		return referent;
	}

	public Collection<AlvisAEAnnotation> getAnnotations() {
		return Collections.unmodifiableCollection(annotations.values());
	}

	public Collection<TextBound> getTextBounds() {
		return Collections.unmodifiableCollection(textBounds);
	}

	public Collection<Group> getGroups() {
		return Collections.unmodifiableCollection(groups);
	}

	public Collection<Relation> getRelations() {
		return Collections.unmodifiableCollection(relations);
	}

	public Collection<SourceAnnotationReference> getUnmatchedReferences() {
		return unmatched.getSourceReferences();
	}
	
	public Collection<SourceAnnotation> getUnmatched() {
		return unmatched.getSources(getDocument());
	}
	
	public void removeTextBound(TextBound t) {
		textBounds.remove(t);
		annotations.remove(t.getId());
	}
	
	public void removeGroup(Group g) {
		groups.remove(g);
		annotations.remove(g.getId());
	}
	
	public void removeRelation(Relation rel) {
		relations.remove(rel);
		annotations.remove(rel.getId());
	}
	
	private List<AnnotationSet> getHeadDependencies() {
		List<AnnotationSet> result = new ArrayList<AnnotationSet>(2);
		for (AnnotationSet aset : dependencies) {
			if (aset.isHead()) {
				result.add(aset);
			}
		}
		return result;
	}
	
	private void updateReferences(Collection<AnnotationSet> headAnnotationSets) {
		for (AlvisAEAnnotation a : annotations.values()) {
			a.updateSources(headAnnotationSets);
		}
		unmatched.update(headAnnotationSets);
	}
	
	void mergeAdjudications(Logger logger) {
		List<AnnotationSet> headDependencies = getHeadDependencies();
		if (headDependencies.size() != 2) {
			throw new RuntimeException("not enough head dependencies");
		}
		logger.fine("updating annotation references");
		updateReferences(headDependencies);
		logger.fine("auto-adjudicating " + toString());
		Iterator<AnnotationSet> asit = headDependencies.iterator();
		AnnotationSet aset1 = asit.next();
		AnnotationSet aset2 = asit.next();
		Map<String,String> forwardSources = buildForwardSources();
		logger.fine("  forward sources: " + forwardSources.size());
		MergeLog mergeLog = new MergeLog(logger);
		AnnotationMerger<TextBound> textBoundMerger = new TextBoundMerger(mergeLog, forwardSources);
		textBoundMerger.merge(aset1, aset2, true);
		mergeLog.report("text-bound");
		AnnotationMerger<Relation> relationMerger = new RelationMerger(mergeLog, forwardSources);
		AnnotationMerger<Group> groupMerger = new GroupMerger(mergeLog, forwardSources);
		boolean firstPass = true;
		while (true) {
			mergeLog.reset();
			boolean proceed = relationMerger.merge(aset1, aset2, firstPass);
			mergeLog.report("relation");
			mergeLog.reset();
			proceed = proceed | groupMerger.merge(aset1, aset2, firstPass); // non short-circuit "or"
			mergeLog.report("group");
			if (proceed) {
				logger.finer("  another round...");
				firstPass = false;
			}
			else {
				break;
			}
		}
		Collection<String> unmatchedIds = buildUnmatchedIds();
		aset1.logStillNeedAdjudication(logger, forwardSources, unmatchedIds);
		aset2.logStillNeedAdjudication(logger, forwardSources, unmatchedIds);
		mergeLog.reportByType();
	}
	
	private void logStillNeedAdjudication(Logger logger, Map<String,String> forwardSources, Collection<String> unmatchedIds) {
		int n = 0;
		logger.fine("still need to be adjudicated (" + user + "):");
		for (AlvisAEAnnotation annot : annotations.values()) {
			String id = annot.getId();
			if (forwardSources.containsKey(id) || unmatchedIds.contains(id)) {
				continue;
			}
			logger.finer("  " + annot);
			n++;
		}
		logger.fine("  total: " + n);
	}
	
	private Collection<String> buildUnmatchedIds() {
		Collection<String> result = new HashSet<String>();
		for (SourceAnnotationReference ref : unmatched.getSourceReferences()) {
			String id = ref.getAnnotationId();
			result.add(id);
		}
		return result;
	}

	private Map<String,String> buildForwardSources() {
		Map<String,String> result = new HashMap<String,String>();
		for (AlvisAEAnnotation a : annotations.values()) {
			String id = a.getId();
			for (SourceAnnotationReference src : a.getSourceReferences()) {
				String srcId = src.getAnnotationId();
				result.put(srcId, id);
			}
		}
		return result;
	}
	
	private static class MergeLog {
		private final Logger logger;
		private int visited = 0;
		private int previouslyMerged = 0;
		private int merged = 0;
		private final CountStats<String> visitedByType = new CountStats<String>(new TreeMap<String,Count>());
		private final CountStats<String> previouslyMergedByType = new CountStats<String>(new TreeMap<String,Count>());
		private final CountStats<String> mergedByType = new CountStats<String>(new TreeMap<String,Count>());
		
		private MergeLog(Logger logger) {
			super();
			this.logger = logger;
		}

		private void report(String prefix) {
			logger.fine(String.format("  %s: %d visited, %d already merged, %d merged", prefix, visited, previouslyMerged, merged));
		}
		
		private void reportByType() {
			Collection<String> types = new TreeSet<String>();
			types.addAll(visitedByType.keySet());
			types.addAll(previouslyMergedByType.keySet());
			types.addAll(mergedByType.keySet());
			logger.finer("  by type:");
			for (String type : types) {
				long visited = visitedByType.safeGet(type).get();
				long previouslyMerged = previouslyMergedByType.safeGet(type).get();
				long merged = mergedByType.safeGet(type).get();
				logger.finer(String.format("    %s: %d visited, %d already merged, %d merged (%.2f%%)", type, visited, previouslyMerged, merged, (100.0 * (previouslyMerged + merged) / visited)));
			}
		}
		
		private void reset() {
			visited = 0;
			previouslyMerged = 0;
			merged = 0;
		}
	}
	
	private static abstract class AnnotationMerger<T extends AlvisAEAnnotation> {
		private final MergeLog mergeLog;
		private final Map<String,String> forwardSources;
		
		protected AnnotationMerger(MergeLog mergeLog, Map<String,String> forwardSources) {
			super();
			this.mergeLog = mergeLog;
			this.forwardSources = forwardSources;
		}

		private boolean merge(AnnotationSet aset1, AnnotationSet aset2, boolean firstPass) {
			boolean result = false;
			for (T annot1 : getCandidates(aset1)) {
				mergeLog.visited++;
				if (firstPass) {
					mergeLog.visitedByType.incr(annot1.getType());
				}
				String id1 = annot1.getId();
				if (forwardSources.containsKey(id1)) {
					mergeLog.previouslyMerged++;
					if (firstPass) {
						mergeLog.previouslyMergedByType.incr(annot1.getType());
					}
					continue;
				}
				T annot2 = lookupIdentical(aset2, annot1);
				if (annot2 != null) {
					mergeLog.merged++;
					mergeLog.mergedByType.incr(annot1.getType());
					String id = UUID.randomUUID().toString();
					T annot = createAnnotation(id, annot1);
					mergeLog.logger.finer("  merged: " + annot.toString().replaceAll("\\s+", " "));
					transferProperties(annot1, annot);
					annot.addSource(annot1, 1);
					annot.addSource(annot2, 2);
					forwardSources.put(id1, id);
					forwardSources.put(annot2.getId(), id);
					result = true;
				}
			}
			return result;
		}
		
		private static void transferProperties(AlvisAEAnnotation source, AlvisAEAnnotation target) {
			for (String key : source.getPropertyKeys()) {
				for (Object value : source.getProperty(key)) {
					target.addProperty(key, value);
				}
			}
		}
		
		private T lookupIdentical(AnnotationSet aset2, T annot1) {
			for (T annot2 : getCandidates(aset2)) {
				if (isIdentical(annot1, annot2)) {
					return annot2;
				}
			}
			return null;
		}
		
		private boolean isIdentical(T annot1, T annot2) {
			return
					annot1.getType().equals(annot2.getType())
					&& _isIdentical(annot1, annot2)
					&& hasSameProperties(annot1, annot2);
		}
		
		protected static <T> boolean identicalCollections(Comparator<T> comp, Collection<T> c1, Collection<T> c2, boolean reorder) {
			if (reorder) {
				return identicalCollections(comp, new TreeSet<T>(c1), new TreeSet<T>(c2), false);
			}
			Iterator<T> it1 = c1.iterator();
			Iterator<T> it2 = c2.iterator();
			while (it1.hasNext()) {
				if (!it2.hasNext()) {
					return false;
				}
				T e1 = it1.next();
				T e2 = it2.next();
				if (comp.compare(e1, e2) != 0) {
					return false;
				}
			}
			return !it2.hasNext();
		}
		
		protected static <T> boolean identicalCollections(Comparator<T> comp, Collection<T> c1, Collection<T> c2) {
			return identicalCollections(comp, c1, c2, false);
		}
		
		protected static <T> boolean identicalCollections(Collection<T> c1, Collection<T> c2, boolean reorder) {
			Comparator<T> comp = Collections.reverseOrder();
			return identicalCollections(comp, c1, c2, reorder);
		}
		
		protected static <T> boolean identicalCollections(Collection<T> c1, Collection<T> c2) {
			return identicalCollections(c1, c2, false);
		}
		
		@SuppressWarnings("unused")
		private static final class PropertyComparator implements Comparator<String> {
			private final AlvisAEAnnotation annot1;
			private final AlvisAEAnnotation annot2;
			
			private PropertyComparator(AlvisAEAnnotation annot1, AlvisAEAnnotation annot2) {
				super();
				this.annot1 = annot1;
				this.annot2 = annot2;
			}

			@Override
			public int compare(String o1, String o2) {
				int r = o1.compareTo(o2);
				if (r != 0) {
					return r;
				}
				Collection<Object> values1 = annot1.getProperty(o1);
				Collection<Object> values2 = annot2.getProperty(o2);
				if (identicalCollections(values1, values2)) {
					return 0;
				}
				return -1;
			}
		}

		@SuppressWarnings("static-method")
		private boolean hasSameProperties(AlvisAEAnnotation annot1, AlvisAEAnnotation annot2) {
			return true;
//			Collection<String> keys1 = annot1.getPropertyKeys();
//			Collection<String> keys2 = annot2.getPropertyKeys();
//			Comparator<String> comp = new PropertyComparator(annot1, annot2);
//			return identicalCollections(comp, keys1, keys2, true);
		}
		
		protected boolean hasSameForward(String id1, String id2) {
			if (!forwardSources.containsKey(id1)) {
				return false;
			}
			if (!forwardSources.containsKey(id2)) {
				return false;
			}
			id1 = forwardSources.get(id1);
			id2 = forwardSources.get(id2);
			return id1.equals(id2);
		}
		
		protected String getForwardSource(String id) {
			return forwardSources.get(id);
		}
		
		protected abstract Collection<T> getCandidates(AnnotationSet aset);
		protected abstract boolean _isIdentical(T annot1, T annot2);
		protected abstract T createAnnotation(String id, T annot1);
	}
	
	private class TextBoundMerger extends AnnotationMerger<TextBound> {
		private TextBoundMerger(MergeLog mergeLog, Map<String,String> forwardSources) {
			super(mergeLog, forwardSources);
		}

		@Override
		protected Collection<TextBound> getCandidates(AnnotationSet aset) {
			return aset.textBounds;
		}

		@Override
		protected TextBound createAnnotation(String id, TextBound annot1) {
			return new TextBound(AnnotationSet.this, id, annot1.getType(), annot1.getFragments());
		}

		@Override
		protected boolean _isIdentical(TextBound annot1, TextBound annot2) {
			Collection<Fragment> frags1 = annot1.getFragments();
			Collection<Fragment> frags2 = annot2.getFragments();
			Comparator<Fragment> comp = new FragmentComparator<Fragment>();
			return identicalCollections(comp, frags1, frags2);
		}
	}

	private class RelationMerger extends AnnotationMerger<Relation> {
		private RelationMerger(MergeLog mergeLog, Map<String,String> forwardSources) {
			super(mergeLog, forwardSources);
		}

		@Override
		protected Collection<Relation> getCandidates(AnnotationSet aset) {
			return aset.relations;
		}

		@Override
		protected Relation createAnnotation(String id, Relation annot1) {
			Relation result = new Relation(AnnotationSet.this, id, annot1.getType());
			for (String role : annot1.getRoles()) {
				String arg1Id = annot1.getArgumentId(role);
				String argId = getForwardSource(arg1Id);
				result.setArgument(role, argId);
			}
			return result;
		}

		@Override
		protected boolean _isIdentical(Relation annot1, Relation annot2) {
			for (String role : annot1.getRoles()) {
				String arg1Id = annot1.getArgumentId(role);
				String arg2Id = annot2.getArgumentId(role);
				if (!hasSameForward(arg1Id, arg2Id)) {
					return false;
				}
			}
			return true;
		}
	}
	
	private class GroupMerger extends AnnotationMerger<Group> {
		private GroupMerger(MergeLog mergeLog, Map<String,String> forwardSources) {
			super(mergeLog, forwardSources);
		}

		@Override
		protected Collection<Group> getCandidates(AnnotationSet aset) {
			return aset.groups;
		}

		@Override
		protected Group createAnnotation(String id, Group annot1) {
			Group result = new Group(AnnotationSet.this, id, annot1.getType());
			for (String item1Id : annot1.getItemIds()) {
				String itemId = getForwardSource(item1Id);
				result.addItem(itemId);
			}
			return result;
		}

		@Override
		protected boolean _isIdentical(Group annot1, Group annot2) {
			Collection<String> items1 = annot1.getItemIds();
			Collection<String> items2 = annot2.getItemIds();
			return identicalCollections(items1, items2, true);
		}
	}

	@Override
	public String toString() {
		return String.format("annotation set %d (doc: %d, task: %s, user: %s)", id, document, task, user);
	}
}
