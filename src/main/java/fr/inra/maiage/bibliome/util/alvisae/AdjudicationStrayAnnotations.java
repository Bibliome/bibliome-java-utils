package fr.inra.maiage.bibliome.util.alvisae;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;

public class AdjudicationStrayAnnotations extends CLIOParser {
	private String host;
	private String user;
	private String database;
	private Logger logger;
	private String schema;
	private int campaignId;
	private int docId;
	private String userName;
	private String taskName;
	
	private class AnnotationSetDispatch {
		private AnnotationSet aSetAdj = null;
		private AnnotationSet aSet1 = null;
		private AnnotationSet aSet2 = null;
		
		private AnnotationSetDispatch(AlvisAEDocument doc) {
			for (AnnotationSet aset : doc.getAnnotationSets()) {
				String t = aset.getTask();
				System.err.println("t = " + t);
				if (t == null) {
					continue;
				}
				if (t.equals(taskName)) {
					if (aSetAdj != null) {
						throw new RuntimeException();
					}
					aSetAdj = aset;
					continue;
				}
				if (aSet1 == null) {
					aSet1 = aset;
					continue;
				}
				if (aSet2 != null) {
					throw new RuntimeException();
				}
				aSet2 = aset;
			}
			if ((aSetAdj == null) || (aSet2 == null)) {
				throw new RuntimeException();
			}
		}
		
		private void addReferencedAnnotations(Collection<AlvisAEAnnotation> result, Collection<SourceAnnotation> sources) {
			for (SourceAnnotation s : sources) {
				result.add(s.getAnnotation());
			}
		}
		
		private Collection<AlvisAEAnnotation> getReferencedAnnotations() {
			Collection<AlvisAEAnnotation> result = new HashSet<AlvisAEAnnotation>();
			for (AlvisAEAnnotation a : aSetAdj.getAnnotations()) {
				addReferencedAnnotations(result, a.getSources());
			}
			addReferencedAnnotations(result, aSetAdj.getUnmatched());
			return result;
		}
	}
	
	private void run(Connection connection) throws SQLException, ParseException {
		AlvisAEDocument doc = load(connection);
		AnnotationSetDispatch aSets = new AnnotationSetDispatch(doc);
		Collection<AlvisAEAnnotation> referencedAnnotations = aSets.getReferencedAnnotations();
		checkAnnotations(referencedAnnotations, aSets.aSet1);
		checkAnnotations(referencedAnnotations, aSets.aSet2);
	}
	
	private final Connection openConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		String url = String.format("jdbc:postgresql://%s/%s", host, database);
		String password = getPassword();
		return DriverManager.getConnection(url, user, password);
	}

	private String getPassword() {
        Console console = System.console();
        char[] result = console.readPassword("Password for %s at %s : ", user, host);
        return new String(result);
	}

	private AlvisAEDocument load(Connection connection) throws SQLException, ParseException {
		Campaign campaign = new Campaign(false, schema, campaignId);
		LoadOptions options = new LoadOptions();
		options.addDocId(docId);
		campaign.loadDocuments(logger, connection, options);
		options.addUserName(userName);
		options.setTaskName(taskName);
		options.setLoadDependencies(true);
		campaign.loadAnnotationSets(logger, connection, options);
		return campaign.getDocuments().iterator().next();
	}

	private static void checkAnnotations(Collection<AlvisAEAnnotation> referencedAnnotations, AnnotationSet aset) {
		System.out.format("Checking annotations from %s (%s, id: %d)\n", aset.getUser(), aset.getTask(), aset.getId());
		PrintAnnotation pa = new PrintAnnotation(System.out);
		for (AlvisAEAnnotation ann : aset.getAnnotations()) {
			if (!referencedAnnotations.contains(ann)) {
				ann.accept(pa, "    ");
			}
		}
	}

	@CLIOption("-host")
	public void setHost(String host) {
		this.host = host;
	}

	@CLIOption("-user")
	public void setUser(String user) {
		this.user = user;
	}

	@CLIOption("-db")
	public void setDatabase(String database) {
		this.database = database;
	}

	@CLIOption("-schema")
	public void setSchema(String schema) {
		this.schema = schema;
	}

	@CLIOption("-campaign")
	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}

	@CLIOption("-doc")
	public void setDocId(int docId) {
		this.docId = docId;
	}

	@CLIOption("-annotator")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@CLIOption("-task")
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		throw new CLIOException("trailing argument: " + arg);
	}

	@Override
	public String getResourceBundleName() {
		return AdjudicationStrayAnnotations.class.getCanonicalName() + "Help";
	}
	
	@CLIOption(value = "-help", stop = true)
	public void help() {
		System.out.print(usage());
	}
	
	public static void main(String[] args) throws CLIOException, ClassNotFoundException, SQLException, ParseException {
		AdjudicationStrayAnnotations inst = new AdjudicationStrayAnnotations();
		if (!inst.parse(args)) {
			try (Connection connection = inst.openConnection()) {
				inst.run(connection);
			}
		}
	}
}
