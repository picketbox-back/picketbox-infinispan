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

import org.infinispan.Cache;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.session.DefaultSessionId;
import org.picketbox.core.session.PicketBoxSession;

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
        super(new DefaultSessionId());
    }

    /**
     * Set the Cache
     *
     * @param theCache
     */
    public void setCache(Cache<Object, Object> theCache) {
        cache = theCache;
        cache.put(id, this);
    }

    @Override
    public void invalidate() throws PicketBoxSessionException {
        super.invalidate();
        cache.evict(id);
    }
}