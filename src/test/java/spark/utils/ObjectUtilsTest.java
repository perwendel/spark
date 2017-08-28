package spark.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ObjectUtilsTest {

    @Test
    public void testIsEmpty_whenArrayIsEmpty() throws Exception {

        assertTrue("Should return false because array is empty", ObjectUtils.isEmpty(new Object[]{}));

    }

    @Test
    public void testIsEmpty_whenArrayIsNotEmpty() throws Exception {

        assertFalse("Should return false because array is not empty", ObjectUtils.isEmpty(new Integer[]{1,2}));

    }

    @Test
    public void testCloseQuietly_null() {
        // Success is not throwing an exception so there's no "assertion" other than not expecting an exception
        ObjectUtils.closeQuietly(null);
    }

    @Test
    public void testCloseQuietly_open() throws Exception {
        // Success is not throwing an exception so there's no "assertion" other than not expecting an exception
        AutoCloseable closeable = mock(AutoCloseable.class);
        doNothing().when(closeable).close();
        ObjectUtils.closeQuietly(closeable);
    }

    @Test
    public void testCloseQuietly_closed() throws Exception {
        // Success is not throwing an exception so there's no "assertion" other than not expecting an exception
        AutoCloseable closeable = mock(AutoCloseable.class);
        doThrow(new IllegalStateException("Already closed")).when(closeable).close();
        ObjectUtils.closeQuietly(closeable);
    }
}