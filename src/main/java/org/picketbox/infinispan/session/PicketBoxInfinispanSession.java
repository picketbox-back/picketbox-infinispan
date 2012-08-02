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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.infinispan.PicketBoxInfinispanMessages;

/**
 * Instance of {@link PicketBoxSession} that internally uses an Infinispan Cache
 *
 * @author anil saldhana
 * @since Aug 2, 2012
 */
public class PicketBoxInfinispanSession extends PicketBoxSession {

    public static final String CONFIG_FILE = "picketbox-infinispan.xml";

    protected Cache<Object, Object> cache = null;

    protected PicketBoxInfinispanSession() {
        try {
            EmbeddedCacheManager manager = new DefaultCacheManager(CONFIG_FILE);
            cache = manager.getCache();
        } catch (IOException e) {
            throw PicketBoxInfinispanMessages.MESSAGES.runtimeException(e);
        }
    }

    @Override
    public void setAttribute(String key, Object val) throws PicketBoxSessionException {
        cache.put(key, val);
    }

    @Override
    public Map<String, Object> getAttributes() throws PicketBoxSessionException {
        Map<String, Object> map = new HashMap<String, Object>();

        Set<Entry<Object, Object>> entries = cache.entrySet();
        int length = entries != null ? entries.size() : 0;
        if (length > 0) {
            for (Entry<Object, Object> entry : entries) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public Object getAttribute(String key) throws PicketBoxSessionException {
        return cache.get(key);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cache.clear();
        cache = null;
    }
}