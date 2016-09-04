package spark.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriPathTest {

    @Test
    public void testSingleDot() {
        String expected = "test/dir/dot/1";
        String test = "././test/dir/./dot/1";

        assertEquals(expected, UriPath.canonical(test));
    }

    @Test
    public void testDoubleDot() {
        String expected = "/d1/doubledot/";
        String test = "/test/d0/../d2/../.././d1/doubledot/down/..";

        assertEquals(expected, UriPath.canonical(test));
    }

    @Test
    public void testUpPastRoot() {
        String expected = null;
        String test = "/r/../s/./../../";

        assertEquals(expected, UriPath.canonical(test));
    }

    @Test
    public void testNormalPath() {
        String expected = "/path/to/dir/";
        String test = "/path/to/dir/";

        assertEquals(expected, UriPath.canonical(test));
    }

    @Test
    public void testSingleFile() {
        String expected = "file.xml";
        String test = "file.xml";

        assertEquals(expected, UriPath.canonical(test));
    }
}
