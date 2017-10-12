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

package fr.inra.maiage.bibliome.util.service;

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
