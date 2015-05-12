package spark.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spark.utils.IOUtils;

public class InputStreamSerializer extends Serializer {

    @Override
    public boolean canHandle(Object element) {
        return element instanceof InputStream;
    }

    @Override
    public void process(OutputStream outputStream, Object element)
            throws IOException {
        String content = IOUtils.toString((InputStream) element);
        outputStream.write(content.getBytes("utf-8"));
    }

}
