package spark.webserver;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public class SparkHttpResponseWrapper extends HttpServletResponseWrapper {
    private final WrappedServletOutputStream output;
    private final PrintWriter writer;

    public SparkHttpResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);

        output = new WrappedServletOutputStream(response.getOutputStream());
        writer = new PrintWriter(output, true);
    }

    public WrappedServletOutputStream getOutput() {
        return output;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public void flushBuffer() throws IOException {
        output.flush();
        writer.flush();
    }
}
