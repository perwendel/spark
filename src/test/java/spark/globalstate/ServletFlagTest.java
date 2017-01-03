package spark.globalstate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class ServletFlagTest {

    @Before
    public void setup() {

        Whitebox.setInternalState(ServletFlag.class, "isRunningFromServlet", new AtomicBoolean(false));
        Whitebox.setInternalState(ServletFlag.class, "contextPath", new AtomicReference<>("/"));
    }

    @Test
    public void testRunFromServlet_whenDefault() throws Exception {

        AtomicBoolean isRunningFromServlet = Whitebox.getInternalState(ServletFlag.class, "isRunningFromServlet");
        assertFalse("Should be false because it is the default value", isRunningFromServlet.get());
    }

    @Test
    public void testRunFromServlet_whenExecuted() throws Exception {

        ServletFlag.runFromServlet("/test");
        AtomicBoolean isRunningFromServlet = Whitebox.getInternalState(ServletFlag.class, "isRunningFromServlet");

        assertTrue("Should be true because it flag has been set after runFromServlet", isRunningFromServlet.get());
        assertEquals("Should expose the context under which the app is deployed", "/test", ServletFlag.getContextPath());
    }

    @Test
    public void testIsRunningFromServlet_whenDefault() throws Exception {

        assertFalse("Should be false because it is the default value", ServletFlag.isRunningFromServlet());
        assertEquals("Should expose the context under which the app is deployed", "/", ServletFlag.getContextPath());

    }

    @Test
    public void testIsRunningFromServlet_whenRunningFromServlet() throws Exception {

        ServletFlag.runFromServlet("/test");
        assertTrue("Should be true because call to runFromServlet has been made", ServletFlag.isRunningFromServlet());
    }
}