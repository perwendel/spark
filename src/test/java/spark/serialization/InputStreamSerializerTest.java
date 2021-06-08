package spark.serialization;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class InputStreamSerializerTest {

    private InputStreamSerializer serializer = new InputStreamSerializer();

    @Test
    public void testProcess_copiesData() throws IOException {
        byte[] bytes = "Hello, Spark!".getBytes();
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        serializer.process(output, input);

        Assert.assertArrayEquals(bytes, output.toByteArray());
    }

    @Test
    public void testProcess_closesStream() throws IOException {
        MockInputStream input = new MockInputStream(new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        serializer.process(output, input);

        Assert.assertTrue("Expected stream to be closed", input.closed);
    }

    private class MockInputStream extends FilterInputStream {

        boolean closed = false;

        private MockInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            super.close();
            closed = true;
        }
    }
}
