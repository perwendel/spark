package spark.webserver;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CharsetFilter implements Filter {

    /** The logger. */
    private org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(getClass());

    private static final String defaultCharset = "utf-8";
    private Set<String> textfileExtensions;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        textfileExtensions = new HashSet<String>();
        textfileExtensions.add(".css");
        textfileExtensions.add(".js");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String requestUri = ((HttpServletRequest) request).getRequestURI()
                .toLowerCase();
        int extIndex = requestUri.lastIndexOf('.');
        if (extIndex != -1
                && textfileExtensions.contains(requestUri.substring(extIndex))) {
            LOG.debug("Setting character encoding to " + defaultCharset);
            response.setCharacterEncoding(defaultCharset);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
