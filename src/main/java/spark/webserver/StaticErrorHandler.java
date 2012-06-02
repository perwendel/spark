package spark.webserver;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.handler.ErrorHandler;

class StaticErrorHandler extends ErrorHandler {

    private static final String FORBIDDEN = "<html><body><h2>403 Forbidden</h2>You don't have permission to access this resource</body></html>";
    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>Spark could not find the requested file</body></html>";

    @Override
    protected void writeErrorPage(HttpServletRequest request, Writer writer,
            int code, String message, boolean showStacks) throws IOException {
        switch (code) {
            case SC_NOT_FOUND:
                writer.write(NOT_FOUND);
                break;
            case SC_FORBIDDEN:
                writer.write(FORBIDDEN);
                break;
            default:
                super.writeErrorPage(request, writer, code, message, showStacks);
                break;
        }
    }
}
