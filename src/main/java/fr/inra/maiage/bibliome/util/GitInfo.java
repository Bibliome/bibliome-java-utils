package fr.inra.maiage.bibliome.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class GitInfo {
	private static final DateFormat GIT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	private final Properties gitVersionProperties;
	private final String canonicalRemoteURL;
	private final String defaultBranch;

	public GitInfo(Properties gitVersionProperties, String canonicalRemoteURL, String defaultBranch) {
		super();
		this.gitVersionProperties = gitVersionProperties;
		this.canonicalRemoteURL = canonicalRemoteURL;
		this.defaultBranch = defaultBranch;
	}

	public GitInfo(Properties gitVersionProperties, String canonicalRemoteURL) {
		this(gitVersionProperties, canonicalRemoteURL, "master");
	}
	
	public GitInfo(String resourceName, String canonicalRemoteURL, String defaultBranch) throws IOException {
		this(new Properties(), canonicalRemoteURL, defaultBranch);
		try (InputStream is = GitInfo.class.getResourceAsStream(resourceName)) {
			this.gitVersionProperties.load(is);
		}
	}
	
	public GitInfo(String resourceName, String canonicalRemoteURL) throws IOException {
		this(new Properties(), canonicalRemoteURL);
		try (InputStream is = GitInfo.class.getResourceAsStream(resourceName)) {
			this.gitVersionProperties.load(is);
		}
	}
	
	private String getStringProperty(String key) {
		return gitVersionProperties.getProperty(key);
	}
	
	private Date getDateProperty(String key) {
		if (gitVersionProperties.containsKey(key)) {
			try {
				return GIT_DATE_FORMAT.parse(gitVersionProperties.getProperty(key));
			}
			catch (ParseException e) {
				return null;
			}
		}
		return null;
	}
	
	private boolean getBooleanProperty(String key) {
		if (gitVersionProperties.containsKey(key)) {
			Strings.getBoolean(gitVersionProperties.getProperty(key), true);
		}
		return true;
	}
	
	private int getIntegerProperty(String key) {
		if (gitVersionProperties.containsKey(key)) {
			return Strings.getInteger(gitVersionProperties.getProperty(key), -1);
		}
		return -1;
	}
	
	public String getCanonicalRemoteURL() {
		return canonicalRemoteURL;
	}

	public String getDefaultBranch() {
		return defaultBranch;
	}
	
	public boolean isDefaultBranch() {
		return defaultBranch.equals(getBranch());
	}

	public String getBranch() {
		return getStringProperty("git.branch");
	}
	
	public String getBuildHost() {
		return getStringProperty("git.build.host");
	}

	public Date getBuildTime() {
		return getDateProperty("git.build.time");
	}

	public String getBuildTimeString() {
		return getStringProperty("git.build.time");
	}
	
	public String getBuildUserEmail() {
		return getStringProperty("git.build.user.email");
	}
	
	public String getBuildUserName() {
		return getStringProperty("git.build.user.name");
	}
	
	public String getBuildVersion() {
		return getStringProperty("git.build.version");
	}
	
	public int getClosestTagCommitCount() {
		return getIntegerProperty("git.closest.tag.commit.count");
	}
	
	public String getClosestTagCommitCountString() {
		return getStringProperty("git.closest.tag.commit.count");
	}
	
	public String getClosestTagName() {
		return getStringProperty("git.closest.tag.name");
	}
	
	public String getCommitId() {
		return getStringProperty("git.commit.id");
	}
	
	public String getCommitIdAbbrev() {
		return getStringProperty("git.commit.id.abbrev");
	}
	
	public String getCommitIdDescribe() {
		return getStringProperty("git.commit.id.describe");
	}
	
	public String getCommitIdDescribeShort() {
		return getStringProperty("git.commit.id.describe-short");
	}
	
	public String getCommitMessageFull() {
		return getStringProperty("git.commit.message.full");
	}
	
	public String getCommitMessageShort() {
		return getStringProperty("git.commit.message.short");
	}
	
	public Date getCommitTime() {
		return getDateProperty("git.commit.time");
	}
	
	public String getCommitTimeString() {
		return getStringProperty("git.commit.time");
	}
	
	public String getCommitUserEmail() {
		return getStringProperty("git.commit.user.email");
	}
	
	public String getCommitUserName() {
		return getStringProperty("git.commit.user.name");
	}
	
	public boolean isDirty() {
		return getBooleanProperty("git.dirty");
	}
	
	public boolean isCanonicalRemoteOrigin() {
		return canonicalRemoteURL.equals(getRemoteOriginURL());
	}
	
	public String getRemoteOriginURL() {
		return getStringProperty("git.remote.origin.url");
	}
	
	public String getTags() {
		return getStringProperty("git.tags");
	}
}
