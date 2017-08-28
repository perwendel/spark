package spark.multipart;

import org.apache.commons.codec.Charsets;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.Part;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletMultipartPartTest {
    private Part servletPart;
    private ServletMultipartPart part;

    @Before
    public void beforeTest() {
        // Each test will use a servlet part but will define its own attributes as needed
        servletPart = mock(Part.class);
        part = new ServletMultipartPart(servletPart);
    }

    @Test
    public void testName() {
        final String name = "foo";

        when(servletPart.getName()).thenReturn(name);

        assertEquals(name, part.name());
    }

    @Test
    public void testFileName_null() {
        when(servletPart.getSubmittedFileName()).thenReturn(null);

        assertNull(part.fileName());
    }

    @Test
    public void testFileName_valid() {
        final String fileName = "foo.bar.zip";

        when(servletPart.getSubmittedFileName()).thenReturn(fileName);

        assertEquals(fileName, part.fileName());
    }

    @Test
    public void testContentType_null() {
        when(servletPart.getContentType()).thenReturn(null);

        assertNull(part.contentType());
    }

    @Test
    public void testContentType_simple() {
        final String type = "application/json";

        when(servletPart.getContentType()).thenReturn(type);

        assertEquals(type, part.contentType());
    }

    @Test
    public void testContentType_withBoundary() {
        final String type = "multipart/form-data; boundary=aabbcc00";

        when(servletPart.getContentType()).thenReturn(type);

        assertEquals(type, part.contentType());
    }

    @Test
    public void testContentLength_zero() {
        final long length = 0;

        when(servletPart.getSize()).thenReturn(length);

        assertEquals(length, part.contentLength());
    }

    @Test
    public void testContentLength_nonZero() {
        final long length = 1024;

        when(servletPart.getSize()).thenReturn(length);

        assertEquals(length, part.contentLength());
    }

    @Test
    public void testStream_nullStream() throws Exception {
        when(servletPart.getInputStream()).thenReturn(null);

        assertNull(part.inputStream());
    }

    @Test
    public void testStream_validStream() throws Exception {
        final InputStream stream = new ByteArrayInputStream(new byte[0]);

        when(servletPart.getInputStream()).thenReturn(stream);

        assertTrue(part.inputStream() == stream);
    }

    @Test
    public void testAsString_nullStream() throws Exception {
        when(servletPart.getInputStream()).thenReturn(null);

        assertEquals("", part.asString());
    }

    @Test
    public void testAsString_validString() throws Exception {
        final String text = "The foo'est, bar'est, baz'iest of them all.";
        final InputStream stream = new ByteArrayInputStream(text.getBytes(Charsets.UTF_8));

        when(servletPart.getInputStream()).thenReturn(stream);

        assertEquals(text, part.asString());
    }

    @Test
    public void testAsString_multipleCalls() throws Exception {
        final String text = "Foo";
        final InputStream stream = new ByteArrayInputStream(text.getBytes(Charsets.UTF_8));

        when(servletPart.getInputStream()).thenReturn(stream);

        // Shouldn't re-read the byte array, so if no IOException is thrown then we're good
        assertEquals(text, part.asString());
        assertEquals(text, part.asString());
    }

    @Test
    public void testAsString_swallowException() throws Exception {
        when(servletPart.getInputStream()).thenThrow(new IOException());

        assertEquals("", part.asString());
    }

    @Test
    public void testAsBytes_null() throws Exception {
        when(servletPart.getInputStream()).thenReturn(null);

        assertArrayEquals(new byte[0], part.asBytes());
    }

    @Test
    public void testAsBytes_validBytes() throws Exception {
        final byte[] bytes = new byte[] { (byte)1, (byte)2, (byte)3 };
        final InputStream stream = new ByteArrayInputStream(bytes);

        when(servletPart.getInputStream()).thenReturn(stream);

        assertArrayEquals(bytes, part.asBytes());
    }

    @Test
    public void testAsBytes_multipleCalls() throws Exception {
        final byte[] bytes = new byte[] { (byte)1, (byte)2, (byte)3 };
        final InputStream stream = new ByteArrayInputStream(bytes);

        when(servletPart.getInputStream()).thenReturn(stream);

        // Should return the same array regardless of how many times you call 'asBytes()'
        assertArrayEquals(bytes, part.asBytes());
        assertArrayEquals(bytes, part.asBytes());
    }

    @Test
    public void testAsBytes_swallowException() throws Exception {
        when(servletPart.getInputStream()).thenThrow(new IOException());

        assertArrayEquals(new byte[0], part.asBytes());
    }
}
