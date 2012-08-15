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
package org.picketbox.test.infinispan.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.junit.Test;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.PicketBoxSessionManager;
import org.picketbox.infinispan.session.PicketBoxInfinispanSession;
import org.picketbox.infinispan.session.PicketBoxInfinispanSessionCreator;

/**
 * Unit test the {@link PicketBoxInfinispanSession}
 *
 * @author anil saldhana
 * @since Aug 2, 2012
 */
public class PicketBoxInfinispanSessionTestCase {

    @Test
    public void testSession() throws Exception {
        PicketBoxSession session = PicketBoxSessionManager.create(PicketBoxInfinispanSessionCreator.class.getName());
        assertTrue(session instanceof PicketBoxInfinispanSession);

        assertTrue(session.isValid());
        // Add something
        session.setAttribute("anil", "security");

        assertEquals("security", session.getAttribute("anil"));

        session.invalidate();

        assertFalse(session.isValid());
    }
}