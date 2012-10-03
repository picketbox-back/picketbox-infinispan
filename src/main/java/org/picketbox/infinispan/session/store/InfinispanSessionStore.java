/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketbox.infinispan.session.store;

import java.io.Serializable;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.picketbox.core.AbstractPicketBoxLifeCycle;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionId;
import org.picketbox.core.session.SessionStore;
import org.picketbox.infinispan.session.CacheListener;

/**
 * <p>
 * Custom {@link SessionStore} implementation using Infinispan to store {@link PicketBoxSession} instances.
 * </p>
 * <p>
 * By default the Infinispan configuration file must exists in the classpath with name picketnlink-ispn.xml and define a
 * namedCache called <b>picketbox-session-cache</b>, if the default constructor is used.
 * </p>
 * <p>
 * To provide a different name for the configuration file or the internal cache name you can use one of the others constructors.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class InfinispanSessionStore extends AbstractPicketBoxLifeCycle implements SessionStore {

    private static final String DEFAULT_CONFIG_FILE = "picketbox-ispn.xml";
    private static final String DEFAULT_CACHE_NAME = "picketbox-session-cache";

    private String configurationFile = DEFAULT_CONFIG_FILE;
    private String cacheName = DEFAULT_CACHE_NAME;

    private Cache<Serializable, PicketBoxSession> cache;

    private DefaultCacheManager cacheManager;

    /**
     * <p>
     * Creates a new instance using the default configuration file: picketbox-ispn.xml.
     * </p>
     */
    public InfinispanSessionStore() {
        this(DEFAULT_CONFIG_FILE);
    }

    /**
     * <p>
     * Creates a new instance using the default configuration file and the specified name for the configured cache. If no cache
     * name is provided the default name will be used.
     * </p>
     */
    public InfinispanSessionStore(String cacheName, String configurationFile) {
        this(configurationFile);
        if (this.cacheName != null) {
            this.cacheName = cacheName;
        }
    }

    /**
     * <p>
     * Creates a new instance using the specified configuration file.
     * </p>
     *
     * @param configurationFile
     */
    public InfinispanSessionStore(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * <p>
     * Loads the configuration and starts the {@link DefaultCacheManager}.
     * </p>
     *
     * @param configurationFile
     */
    private void startCache(String configurationFile) {
        try {
            this.cacheManager = new DefaultCacheManager(configurationFile);

            cacheManager.start();

            this.cache = cacheManager.getCache(this.cacheName);

            this.cache.addListener(new CacheListener());
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing ISPN cache configuration.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionStore#load(org.picketbox.core.session.SessionId)
     */
    @Override
    public PicketBoxSession load(SessionId<? extends Serializable> key) {
        return this.cache.get(key.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionStore#store(org.picketbox.core.session.PicketBoxSession)
     */
    @Override
    public void store(PicketBoxSession session) {
        this.cache.put(session.getId().getId(), session);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionStore#remove(org.picketbox.core.session.SessionId)
     */
    @Override
    public void remove(SessionId<? extends Serializable> id) {
        this.cache.remove(id.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionStore#update(org.picketbox.core.session.PicketBoxSession)
     */
    @Override
    public void update(PicketBoxSession session) {
        this.cache.put(session.getId().getId(), session);
    }

    @Override
    protected void doStart() {
        startCache(this.configurationFile);
    }

    @Override
    protected void doStop() {
        this.cacheManager.stop();
    }

}