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
package org.picketbox.infinispan.session;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.PicketBoxSessionCreator;
import org.picketbox.infinispan.PicketBoxInfinispanMessages;

/**
 * An instance of {@link PicketBoxSessionCreator} that creates Infinispan enabled sessions
 *
 * @author anil saldhana
 * @since Aug 2, 2012
 */
public class PicketBoxInfinispanSessionCreator implements PicketBoxSessionCreator {
    public static final String CONFIG_FILE = "picketbox-infinispan.xml";

    protected Cache<Object, Object> cache = null;

    public PicketBoxInfinispanSessionCreator() {
        try {
            EmbeddedCacheManager manager = new DefaultCacheManager(CONFIG_FILE);
            cache = manager.getCache();
            cache.addListener(new CacheListener());
        } catch (IOException e) {
            throw PicketBoxInfinispanMessages.MESSAGES.runtimeException(e);
        }
    }

    @Override
    public PicketBoxSession create() {
        PicketBoxInfinispanSession session = new PicketBoxInfinispanSession();
        session.setCache(cache);
        return session;
    }
}