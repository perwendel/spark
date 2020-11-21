/*
 * Copyright 2016- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.servlet;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.ExceptionMapper;
import spark.globalstate.ServletFlag;
import spark.http.matching.MatcherFilter;
import spark.route.ServletRoutes;
import spark.staticfiles.StaticFilesConfiguration;
import spark.utils.CollectionUtils;
import spark.utils.StringUtils;

/**
 * Servlet that can be configured through a web.xml to serve {@code SparkApplication}s.
 * Needs to be initialized with the {@code applicationName} init parameter (or through a direct
 * call to {@link #setSparkApplications(List)}) to the list of application classes defining routes.
 * An optional {@code wrapperSupplierName} parameter can be provided (or directly set through the
 * {@link #setRequestSupplier(BiFunction)} method) to provide added path flexibility.
 *
 * @author R.A. Porter
 */
public class SparkServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkServlet.class);

    private static final String SLASH_WILDCARD = "/*";

    private static final String SLASH = "/";

    private static final String APPLICATION_CLASS_PARAM = "applicationName";

    private static final String WRAPPER_SUPPLIER_PARAM_NAME = "wrapperSupplierName";

    private static final String FILTER_MAPPING_PARAM = "filterMappingUrlPattern";

    private static final BiFunction<HttpServletRequest, String, HttpServletRequestWrapper>
            DEFAULT_REQ_FUNC =
            new BiFunction<HttpServletRequest, String, HttpServletRequestWrapper>() {
                @Override
                public HttpServletRequestWrapper apply(HttpServletRequest req,
                        String relativePath) {
                    return new HttpServletRequestWrapper(req) {
                        @Override
                        public String getPathInfo() {
                            return relativePath;
                        }

                        @Override
                        public String getRequestURI() {
                            return relativePath;
                        }
                    };
                }
            };

    private BiFunction<HttpServletRequest, String, HttpServletRequestWrapper> requestSupplier;

    private final List<SparkApplication> sparkApplications =
            Collections.synchronizedList(new ArrayList<>());

    private String filterMappingPattern = null;

    private String filterPath;

    private MatcherFilter matcherFilter;

    public synchronized void setRequestSupplier(
            BiFunction<HttpServletRequest, String, HttpServletRequestWrapper> requestSupplier) {
        this.requestSupplier = requestSupplier;
    }

    public void setSparkApplications(List<SparkApplication> sparkApplications) {
        this.sparkApplications.addAll(sparkApplications);
    }

    public void setFilterMappingPattern(String filterMappingPattern) {
        this.filterMappingPattern = filterMappingPattern;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletFlag.runFromServlet();

        populateWrapperSupplier(config);
        populateSparkApplications(config);

        sparkApplications.stream()
                .sequential()
                .forEach(SparkApplication::init);

        filterPath = getConfigPath(filterMappingPattern, config);
        matcherFilter = new MatcherFilter(ServletRoutes.get(),
                StaticFilesConfiguration.servletInstance,
                ExceptionMapper.getServletInstance(),
                false,
                false);
    }

    @Override
    public void destroy() {
        sparkApplications.stream()
                .filter(Objects::nonNull)
                .forEach(SparkApplication::destroy);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final String relativePath = getRelativePath(req, filterPath);

        HttpServletRequestWrapper requestWrapper;
        synchronized (this) {
            requestWrapper = requestSupplier.apply(req, relativePath);
        }

        // handle static resources
        boolean consumed = StaticFilesConfiguration.servletInstance.consume(req, resp);

        if (consumed) {
            return;
        }

        matcherFilter.doFilter(requestWrapper, resp, null);
    }

    private static String getConfigPath(String filterMappingPattern, ServletConfig config) {
        String result = Optional.ofNullable(filterMappingPattern)
                .orElse(config.getInitParameter(FILTER_MAPPING_PARAM));
        if (result == null || result.equals(SLASH_WILDCARD)) {
            return "";
        } else if (!result.startsWith(SLASH) || !result.endsWith(SLASH_WILDCARD)) {
            throw new IllegalArgumentException(String.format(
                    "The %s must start with '/' and end with '/*'. Instead it is: %s",
                    FILTER_MAPPING_PARAM,
                    result));
        }
        return result.substring(1, result.length() - 1);
    }

    private static String getRelativePath(HttpServletRequest request, String filterPath) {
        String path = request.getRequestURI()
                .substring(request.getContextPath()
                        .length());

        if (path.length() > 0) {
            path = path.substring(1);
        }

        if (filterPath.equals(path + SLASH)) {
            path += SLASH;
        }

        if (path.startsWith(filterPath)) {
            path = path.substring(filterPath.length());
        }

        if (!path.startsWith(SLASH)) {
            path = SLASH + path;
        }

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            // this can't really ever happen
        }

        LOGGER.debug("Relative path = {}", path);
        return path;
    }

    private synchronized void populateWrapperSupplier(ServletConfig config) {
        // Do not override an injected supplier through initialization
        if (requestSupplier != null) {
            return;
        }

        String wrapperSupplierName = config.getInitParameter(WRAPPER_SUPPLIER_PARAM_NAME);

        if (StringUtils.isNotBlank(wrapperSupplierName)) {
            try {
                Class<?> wrapperClass = Class.forName(wrapperSupplierName);
                if (BiFunction.class.isAssignableFrom(wrapperClass)) {
                    requestSupplier =
                            (BiFunction<HttpServletRequest, String, HttpServletRequestWrapper>) wrapperClass.newInstance();
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                LOGGER.debug(
                        "Error converting {} to BiFunction<HttpServletRequest, String, HttpServletRequestWrapper>; "
                                + "falling back to default",
                        wrapperSupplierName,
                        e);
            }

        }

        if (requestSupplier == null) {
            requestSupplier = DEFAULT_REQ_FUNC;
        }
    }

    private void populateSparkApplications(ServletConfig config) {
        // Do not override injected spark applications through initialization
        if (!CollectionUtils.isEmpty(sparkApplications)) {
            return;
        }

        String applications = config.getInitParameter(APPLICATION_CLASS_PARAM);

        if (StringUtils.isNotBlank(applications)) {
            sparkApplications.addAll(Pattern.compile(",")
                    .splitAsStream(applications)
                    .map(String::trim)
                    .map(this::getApplication)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
    }

    private SparkApplication getApplication(String applicationClassName) {
        try {
            Class<?> appClass = Class.forName(applicationClassName);
            if (SparkApplication.class.isAssignableFrom(appClass)) {
                return SparkApplication.class.cast(appClass.newInstance());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.debug("Error converting {} to SparkApplication", applicationClassName, e);
        }

        return null;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new NotSerializableException(getClass().getName());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(getClass().getName());
    }
}
