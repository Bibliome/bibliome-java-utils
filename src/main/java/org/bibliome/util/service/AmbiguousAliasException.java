package org.bibliome.util.service;

import java.util.List;

import org.bibliome.util.Strings;

/**
 * Thrown by getServiceByAlias() if the alias resolves to several keys.
 */
public class AmbiguousAliasException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public AmbiguousAliasException(String alias, List<?> keys) {
        super(alias + ": " + Strings.joinStrings(keys, ", "));
    }
}