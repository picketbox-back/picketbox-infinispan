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

package org.picketbox.test.infinispan.session.store;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionId;
import org.picketbox.infinispan.session.store.DistributableSessionStore;

/**
 * <p>
 * Tests the core functionality for the {@link DistributableSessionStore}.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class DistributableSessionStoreTestCase {

    /**
     * <p>
     * Tests the {@link PicketBoxSession} replication between two distinct {@link PicketBoxManager} instances.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testSessionReplication() throws Exception {
        DefaultPicketBoxManager firstPicketBoxManager = createPicketBoxManager();

        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        // lets authenticated the subject and get its session to use later
        PicketBoxSubject authenticatedSubject = firstPicketBoxManager.authenticate(subject);
        PicketBoxSession session = authenticatedSubject.getSession();
        SessionId<? extends Serializable> sessionId = session.getId();

        assertTrue(authenticatedSubject.isAuthenticated());

        DefaultPicketBoxManager secondPicketBoxManager = createPicketBoxManager();

        // lets try to authenticate the subject using the id from the previous session
        PicketBoxSubject sameSessionSubject = new PicketBoxSubject(sessionId);
        PicketBoxSubject replicatedSubject = secondPicketBoxManager.authenticate(sameSessionSubject);

        assertTrue(replicatedSubject.isAuthenticated());
    }

    /**
     * <p>
     * Tests if attributes are properly replicated.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testSessionAttributeReplication() throws Exception {
        DefaultPicketBoxManager firstPicketBoxManager = createPicketBoxManager();

        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        // lets authenticated the subject and get its session to use later
        PicketBoxSubject authenticatedSubject = firstPicketBoxManager.authenticate(subject);
        PicketBoxSession session = authenticatedSubject.getSession();
        SessionId<? extends Serializable> sessionId = session.getId();

        assertTrue(authenticatedSubject.isAuthenticated());

        DefaultPicketBoxManager secondPicketBoxManager = createPicketBoxManager();

        // lets try to authenticate the subject using the id from the previous session
        PicketBoxSubject sameSessionSubject = new PicketBoxSubject(sessionId);
        PicketBoxSubject replicatedSubject = secondPicketBoxManager.authenticate(sameSessionSubject);

        assertTrue(replicatedSubject.isAuthenticated());

        // lets replicate the attributes
        PicketBoxSession replicatedSession = replicatedSubject.getSession();

        replicatedSession.setAttribute("attributeA", "attributeA");

        assertNotNull(session.getAttribute("attributeA"));

        session.setAttribute("attributeA", "changed");

        assertNotNull(replicatedSession.getAttribute("attributeA"));
        assertEquals("changed", replicatedSession.getAttribute("attributeA"));
    }

    /**
     * <p>
     * Tests if when used a invalid session id the authentication will fail.
     * </p>
     * 
     * @throws Exception
     */
    @Test(expected = AuthenticationException.class)
    public void testSessionInvalidation() throws Exception {
        DefaultPicketBoxManager firstPicketBoxManager = createPicketBoxManager();
        DefaultPicketBoxManager secondPicketBoxManager = createPicketBoxManager();

        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject authenticatedSubject = firstPicketBoxManager.authenticate(subject);
        PicketBoxSession session = authenticatedSubject.getSession();
        SessionId<? extends Serializable> sessionId = session.getId();

        PicketBoxSubject sameSessionSubject = new PicketBoxSubject(sessionId);
        PicketBoxSubject replicatedSubject = secondPicketBoxManager.authenticate(sameSessionSubject);

        // user logout. now the session should be invalidated and removed from the cache.
        firstPicketBoxManager.logout(authenticatedSubject);

        assertFalse(replicatedSubject.isAuthenticated());

        // lets try to authenticated again with an invalid session id
        PicketBoxSubject invalidSessionSubject = new PicketBoxSubject(sessionId);

        secondPicketBoxManager.authenticate(invalidSessionSubject);
    }
    
    /**
     * <p>
     * Tests if when used a invalid session id the authentication will fail.
     * </p>
     * 
     * @throws Exception
     */
    @Test(expected = AuthenticationException.class)
    public void testSessionExpiration() throws Exception {
        DefaultPicketBoxManager firstPicketBoxManager = createPicketBoxManager();
        DefaultPicketBoxManager secondPicketBoxManager = createPicketBoxManager();

        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject authenticatedSubject = firstPicketBoxManager.authenticate(subject);
        PicketBoxSession session = authenticatedSubject.getSession();
        SessionId<? extends Serializable> sessionId = session.getId();

        PicketBoxSubject sameSessionSubject = new PicketBoxSubject(sessionId);
        PicketBoxSubject replicatedSubject = secondPicketBoxManager.authenticate(sameSessionSubject);

        assertTrue(replicatedSubject.isAuthenticated());
        
        Thread.sleep(65000);
        
        // lets try to authenticated again with an invalid session id
        PicketBoxSubject invalidSessionSubject = new PicketBoxSubject(sessionId);

        secondPicketBoxManager.authenticate(invalidSessionSubject);
    }

    private DefaultPicketBoxManager createPicketBoxManager() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder
            .sessionManager()
                .store(new DistributableSessionStore())
                .sessionTimeout(1);

        PicketBoxConfiguration configuration = builder.build();

        DefaultPicketBoxManager picketBoxManager = new DefaultPicketBoxManager(configuration);

        picketBoxManager.start();

        return picketBoxManager;
    }

}
