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

package org.bibliome.util.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A service factory base class.
 */
public abstract class AbstractServiceFactory<K, S> implements ServiceFactory<K,S> {
    private final Set<K> supportedServices = new HashSet<K>();

    /**
     * Adds the supported service.
     * @param key
     */
    protected void addSupportedService(K key) {
        supportedServices.add(key);
    }

    @Override
    public void addAlias(String alias, K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public S getServiceByAlias(String alias) throws UnsupportedServiceException, AmbiguousAliasException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupported(K key) {
        return supportedServices.contains(key);
    }

    @Override
    public List<K> resolveAlias(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<K> supportedServices() {
        return Collections.unmodifiableCollection(supportedServices);
    }
}
