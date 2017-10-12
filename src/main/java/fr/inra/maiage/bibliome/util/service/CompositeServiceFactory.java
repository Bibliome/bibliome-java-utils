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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * A service factory composed with several service factories.
 */
public abstract class CompositeServiceFactory<K,S,F extends ServiceFactory<K,S>> implements ServiceFactory<K,S> {
    private final Map<K,F> factories = new HashMap<K,F>();
    private final Map<String,List<K>> aliases = new HashMap<String,List<K>>();

    /**
     * Adds a compound factory.
     * @param factory
     * @param logger
     */
    public void addFactory(F factory, Logger logger) {
        if (factories.values().contains(factory)) {
            if (logger != null)
                logger.warning("compound service factory has already factory " + factory);
            return;
        }
        for (K key : factory.supportedServices()) {
            if (factories.containsKey(key)) {
                if (logger != null)
                    logger.warning("compound service factory has already factory for " + key);
                continue;
            }
            factories.put(key, factory);
            for (String alias : autoAlias(key)) {
                // System.err.println("add alias = " + alias);
                addAlias(alias, key);
            }
        }
    }

    /**
     * Adds a compound factory.
     * @param factory
     */
    public void addFactory(F factory) {
        addFactory(factory, null);
    }

//    @Override
//    public S getService(K key, Properties properties) throws UnsupportedServiceException {
//        if (!factories.containsKey(key))
//            throw new UnsupportedServiceException("key: " + key);
//        return factories.get(key).getService(key, properties);
//    }

    @Override
    public S getService(K key) throws UnsupportedServiceException {
        if (!factories.containsKey(key))
            throw new UnsupportedServiceException("key: " + key);
        return factories.get(key).getService(key);
    }

    @Override
    public boolean isSupported(K key) {
        return factories.containsKey(key);
    }

    @Override
    public Collection<K> supportedServices() {
        return Collections.unmodifiableCollection(factories.keySet());
    }

    @Override
    public void addAlias(String alias, K key) {
        List<K> keyAliases;
        if (aliases.containsKey(alias)) {
            keyAliases = aliases.get(alias);
            if (keyAliases.contains(key))
                return;
        }
        else {
            keyAliases = new ArrayList<K>(1);
            aliases.put(alias, keyAliases);
        }
        keyAliases.add(key);
    }

    @Override
    public List<K> resolveAlias(String name) {
        if (aliases.containsKey(name))
            return Collections.unmodifiableList(aliases.get(name));
        return Collections.emptyList();
    }

//    @Override
//    public S getServiceByAlias(String alias, Properties properties) throws UnsupportedServiceException, AmbiguousAliasException {
//        // System.err.println("alias = " + alias);
//        if (!aliases.containsKey(alias))
//            throw new UnsupportedServiceException("alias: " + alias);
//        List<K> keys = aliases.get(alias);
//        if (keys.size() > 1)
//            throw new AmbiguousAliasException(alias, keys);
//        return getService(keys.get(0), properties);
//    }

    @Override
    public S getServiceByAlias(String alias) throws UnsupportedServiceException, AmbiguousAliasException {
        // System.err.println("alias = " + alias);
        if (!aliases.containsKey(alias))
            throw new UnsupportedServiceException("alias: " + alias);
        List<K> keys = aliases.get(alias);
        if (keys.size() > 1)
            throw new AmbiguousAliasException(alias, keys);
        return getService(keys.get(0));
    }

    /**
     * Adds factories seen by a service loader.
     * @param serviceFactoryClass
     * @param include
     * @param exclude
     * @param logger
     */
    public void loadServiceFactories(Class<? extends F> serviceFactoryClass, Collection<String> include, Collection<String> exclude, Logger logger) {
        // System.err.println("load service = " + serviceFactoryClass);
        for (F sf : ServiceLoader.load(serviceFactoryClass)) {
            // System.err.println("service = " + sf);
            String fn = sf.getClass().getCanonicalName();
            if ((exclude != null) && exclude.contains(fn))
                continue;
            if ((include != null) && !include.contains(fn))
                continue;
            addFactory(sf, logger);
        }
    }

    /**
     * Adds factories seen by a service loader.
     * @param serviceFactoryClass
     * @param logger
     */
    public void loadServiceFactories(Class<? extends F> serviceFactoryClass, Logger logger) {
        loadServiceFactories(serviceFactoryClass, null, null, logger);
    }

    /**
     * Adds factories seen by a service loader.
     * @param serviceFactoryClass
     * @param include
     * @param exclude
     */
    public void loadServiceFactories(Class<? extends F> serviceFactoryClass, Collection<String> include, Collection<String> exclude) {
        loadServiceFactories(serviceFactoryClass, include, exclude, null);
    }

    /**
     * Adds factories seen by a service loader.
     * @param serviceFactoryClass
     */
    public void loadServiceFactories(Class<? extends F> serviceFactoryClass) {
        loadServiceFactories(serviceFactoryClass, null, null, null);
    }

    /**
     * Automatic alias generation (called by addFactory() methods).
     * @param key
     */
    public abstract Collection<String> autoAlias(K key);
  
    /**
     * Returns the compound factories.
     */
    protected Collection<F> getServiceFactories() {
    	return Collections.unmodifiableCollection(factories.values());
    }
}
