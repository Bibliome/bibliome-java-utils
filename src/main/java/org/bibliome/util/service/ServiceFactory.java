package org.bibliome.util.service;

import java.util.Collection;
import java.util.List;


/**
 * A service factory can offer a new instance of a class if given a key.
 */
public interface ServiceFactory<K, S> {
    /**
     * Returns a new instance of the service associated to the specified key.
     * @param key
     * @throws UnsupportedServiceException
     * @throws ServiceInstanciationException
     */
    public S getService(K key) throws UnsupportedServiceException;

    /**
     * Returns a new instance of the service associated to the specified key and using the specified properties.
     * @param key
     * @param properties
     * @throws UnsupportedServiceException
     * @throws ServiceInstanciationException
     */
//    public S getService(K key, Properties properties) throws UnsupportedServiceException;
    
    /**
     * Returns all possible keys corresponding to the specified alias.
     * @param name
     */
    public List<K> resolveAlias(String name);

    /**
     * Returns either if a service is supported for the specified key.
     * @param key
     */
    public boolean isSupported(K key);

    /**
     * Returns all keys for which a service is supported.
     * @return the collection< k>
     */
    public Collection<K> supportedServices();

    /**
     * Adds an alias.
     * @param alias
     * @param key
     */
    public void addAlias(String alias, K key);

    /**
     * Returns a new service instance corresponding to the specified alias.
     * @param alias
     * @throws UnsupportedServiceException
     * @throws ServiceInstanciationException
     * @throws AmbiguousAliasException
     */
    public S getServiceByAlias(String alias) throws UnsupportedServiceException, AmbiguousAliasException;
}
