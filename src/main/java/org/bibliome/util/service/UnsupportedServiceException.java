package org.bibliome.util.service;

/**
 * Thrown if there is no service associated to a given key or alias.
 */
public class UnsupportedServiceException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public UnsupportedServiceException(String msg) {
        super(msg);
    }
}