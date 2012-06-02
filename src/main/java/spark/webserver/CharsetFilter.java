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

/**
 * Workaround for <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=347090">this bug</a>
 * 
 */
class CharsetFilter implements Filter {

    /** The logger. */
    private org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(getClass());

    private String defaultCharset;
    private Set<String> filenameExtensions;

    public CharsetFilter(String charset) {
        assert charset != null;
        defaultCharset = charset;
        filenameExtensions = new HashSet<String>();
        filenameExtensions.add(".css");
        filenameExtensions.add(".js");
        filenameExtensions.add(".html");
        filenameExtensions.add(".htm");
        filenameExtensions.add(".xml");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String requestUri = ((HttpServletRequest) request).getRequestURI()
                .toLowerCase();
        int extIndex = requestUri.lastIndexOf('.');
        if (extIndex != -1
                && filenameExtensions.contains(requestUri.substring(extIndex))) {
            LOG.debug(String.format("Forcing character encoding of '%s' to %s",
                    requestUri, defaultCharset));
            response.setCharacterEncoding(defaultCharset);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
