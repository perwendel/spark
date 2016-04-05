package spark;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SessionTest {

    Request request;
    HttpSession httpSession;
    Session session;

    @Before
    public void setup() {

        httpSession = mock(HttpSession.class);
        request = mock(Request.class);
        session = new Session(httpSession, request);
    }

    @Test
    public void testSession_whenHttpSessionIsNull_thenThrowException() {

        try {

            new Session(null, request);
            fail("Session instantiation with a null HttpSession should throw an IllegalArgumentException");

        } catch (IllegalArgumentException ex) {

            assertEquals("session cannot be null", ex.getMessage());
        }
    }

    @Test
    public void testSession_whenRequestIsNull_thenThrowException() {

        try {

            new Session(httpSession, null);
            fail("Session instantiation with a null Request should throw an IllegalArgumentException");

        } catch (IllegalArgumentException ex) {

            assertEquals("request cannot be null", ex.getMessage());
        }
    }

    @Test
    public void testSession() {

        HttpSession internalSession = Whitebox.getInternalState(session, "session");
        assertEquals("Internal session should be set to the http session provided during instantiation",
                httpSession, internalSession);
    }

    @Test
    public void testRaw() {

        assertEquals("Should return the HttpSession provided during instantiation",
                httpSession, session.raw());
    }

    @Test
    public void testAttribute_whenAttributeIsRetrieved() {

        when(httpSession.getAttribute("name")).thenReturn("Jett");

        assertEquals("Should return attribute from HttpSession", "Jett", session.attribute("name"));

    }

    @Test
    public void testAttribute_whenAttributeIsSet() {

        session.attribute("name", "Jett");

        verify(httpSession).setAttribute("name", "Jett");
    }

    @Test
    public void testAttributes() {

        Set<String> attributes = new HashSet<>(Arrays.asList("name", "location"));

        when(httpSession.getAttributeNames()).thenReturn(Collections.enumeration(attributes));

        assertEquals("Should return attributes from the HttpSession", attributes, session.attributes());
    }

    @Test
    public void testCreationTime() {

        when(httpSession.getCreationTime()).thenReturn(10000000l);

        assertEquals("Should return creationTime from HttpSession", 10000000l, session.creationTime());
    }

    @Test
    public void testId() {

        when(httpSession.getId()).thenReturn("id");

        assertEquals("Should return session id from HttpSession", "id", session.id());
    }

    @Test
    public void testLastAccessedTime() {

        when(httpSession.getLastAccessedTime()).thenReturn(20000000l);

        assertEquals("Should return lastAccessedTime from HttpSession", 20000000l, session.lastAccessedTime());
    }

    @Test
    public void testMaxInactiveInterval_whenRetrieved() {

        when(httpSession.getMaxInactiveInterval()).thenReturn(100);

        assertEquals("Should return maxInactiveInterval from HttpSession", 100, session.maxInactiveInterval());
    }

    @Test
    public void testMaxInactiveInterval_whenSet() {

        session.maxInactiveInterval(200);

        verify(httpSession).setMaxInactiveInterval(200);
    }

    @Test
    public void testInvalidate() {

        session.invalidate();

        verify(httpSession).invalidate();
    }

    @Test
    public void testIsNew() {

        when(httpSession.isNew()).thenReturn(true);

        assertEquals("Should return isNew status from HttpSession", true, session.isNew());
    }

    @Test
    public void testRemoveAttribute() {

        session.removeAttribute("name");

        verify(httpSession).removeAttribute("name");
    }
}