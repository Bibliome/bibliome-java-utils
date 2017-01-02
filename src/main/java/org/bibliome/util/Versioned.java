package org.bibliome.util;

import java.util.ResourceBundle;

/**
 * Object that has a version.
 */
public class Versioned {
    private final String version;
    private final String revision;
    private final String buildDate;

    /**
     * Instantiates a new versioned object with the specified version, revision and buiuld date.
     * @param version
     * @param revision
     * @param buildDate
     */
    public Versioned(String version, String revision, String buildDate) {
        this.version = version;
        this.revision = revision;
        this.buildDate = buildDate;
    }

    /**
     * Instantiates a new versioned object.
     * The version, revision and build date will be taken from the specified resource bundle with the specified keys.
     * @param bundle
     * @param version
     * @param revision
     * @param buildDate
     */
    public Versioned(ResourceBundle bundle, String version, String revision, String buildDate) {
        this(bundle.getString(version), bundle.getString(revision), bundle.getString(buildDate));
    }

    /**
     * Instantiates a new versioned object.
     * this(bundle, "version", "revision", "buildDate").
     * @param bundle
     */
    public Versioned(ResourceBundle bundle) {
        this(bundle, "version", "revision", "buildDate");
    }

    /**
     * Instantiates a new versioned object.
     * The version, revision and build date will be taken from the resource bundle with the specified name and keys.
     * @param bundleName
     * @param version
     * @param revision
     * @param buildDate
     */
    public Versioned(String bundleName, String version, String revision, String buildDate) {
        this(ResourceBundle.getBundle(bundleName), version, revision, buildDate);
    }

    /**
     * Instantiates a new versioned object.
     * this(bundleName, "version", "revision", "buildDate").
     * @param bundleName
     */
    public Versioned(String bundleName) {
        this(ResourceBundle.getBundle(bundleName));
    }

    /**
     * Returns the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the revision.
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Returns the buid date.
     */
    public String getBuildDate() {
        return buildDate;
    }

    @Override
    public String toString() {
        return String.format("version %s, revision %s, build at %s", version, revision, buildDate);
    }
}
