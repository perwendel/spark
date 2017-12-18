/*
 * Copyright 2011- Per Wendel
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
package spark.http.matching;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.CustomErrorPages;
import spark.HaltException;
import spark.RequestResponseFactory;
import spark.Response;
import spark.embeddedserver.jetty.HttpRequestWrapper;
import spark.route.HttpMethod;
import spark.serialization.SerializerChain;
import spark.staticfiles.StaticFilesConfiguration;

/**
 * Matches Spark routes and filters.
 *
 * @author Per Wendel
 */
public class MatcherFilter implements Filter {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MatcherFilter.class);

    private static final String ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept";
    private static final String HTTP_METHOD_OVERRIDE_HEADER = "X-HTTP-Method-Override";

    private final StaticFilesConfiguration staticFiles;

    private spark.route.Routes routeMatcher;
    private SerializerChain serializerChain;

    private boolean externalContainer;
    private boolean hasOtherHandlers;

    /**
     * Constructor
     *
     * @param routeMatcher      The route matcher
     * @param staticFiles       The static files configuration object
     * @param externalContainer Tells the filter that Spark is run in an external web container.
     *                          If true, chain.doFilter will be invoked if request is not consumed by Spark.
     * @param hasOtherHandlers  If true, do nothing if request is not consumed by Spark in order to let others handlers process the request.
     */
    public MatcherFilter(spark.route.Routes routeMatcher,
                         StaticFilesConfiguration staticFiles,
                         boolean externalContainer,
                         boolean hasOtherHandlers) {

        this.routeMatcher = routeMatcher;
        this.staticFiles = staticFiles;
        this.externalContainer = externalContainer;
        this.hasOtherHandlers = hasOtherHandlers;
        this.serializerChain = new SerializerChain();
    }

    @Override
    public void init(FilterConfig config) {
        //
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // handle static resources
        boolean consumedByStaticFile = staticFiles.consume(httpRequest, httpResponse);

        if (consumedByStaticFile) {
            return;
        }

        String method = getHttpMethodFrom(httpRequest);

        String httpMethodStr = method.toLowerCase();
        String uri = httpRequest.getRequestURI();
        String acceptType = httpRequest.getHeader(ACCEPT_TYPE_REQUEST_MIME_HEADER);

        Body body = Body.create();

        RequestWrapper requestWrapper = RequestWrapper.create();
        ResponseWrapper responseWrapper = ResponseWrapper.create();

        Response response = RequestResponseFactory.create(httpResponse);

        HttpMethod httpMethod = HttpMethod.get(httpMethodStr);

        RouteContext context = RouteContext.create()
                .withMatcher(routeMatcher)
                .withHttpRequest(httpRequest)
                .withUri(uri)
                .withAcceptType(acceptType)
                .withBody(body)
                .withRequestWrapper(requestWrapper)
                .withResponseWrapper(responseWrapper)
                .withResponse(response)
                .withHttpMethod(httpMethod);

        Optional<CompletableFuture> future = Optional.empty();

        try {

            BeforeFilters.execute(context);
            future = Routes.execute(context);

        } catch (HaltException halt) {

            Halt.modify(httpResponse, body, halt);

        } catch (Exception generalException) {

            GeneralError.modify(
                    httpRequest,
                    httpResponse,
                    body,
                    requestWrapper,
                    responseWrapper,
                    generalException);
        }

        if(future.isPresent()){
            AsyncContext ac = httpRequest.startAsync();
            future.get().exceptionally(ex -> {
                System.out.println("ex: "+ex);
                handleRouteException(context, (Exception) ex);
                processRouteResult(context, chain);
                ac.complete();
                return ex;
            });
            future.get().thenAccept(result -> {
                processRouteResult(context, chain);
                ac.complete();
            });
        } else {
            processRouteResult(context, chain);
        }
    }

    private void handleRouteException(RouteContext context, Exception ex) {
        if(ex instanceof HaltException){
            Halt.modify(context.responseWrapper().raw(), context.body(), (HaltException) ex);
        } else {
            GeneralError.modify(
                context.httpRequest(),
                context.responseWrapper().raw(),
                context.body(),
                context.requestWrapper(),
                context.responseWrapper(),
                ex);
        }
    }

    private void processRouteResult(RouteContext context, FilterChain chain){
        afterFilter(context);
        checkBodyResult(context);
        afterAfterFilter(context);
        try {
            bodySerialize(context, chain);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    private void afterFilter(RouteContext context){
        try{
            AfterFilters.execute(context);

        } catch (Exception ex) {
            handleRouteException(context, ex);

        }
    }

    private void bodySerialize(RouteContext context, FilterChain chain) throws IOException, ServletException {
        if (context.body().isSet()) {
            context.body().serializeTo(context.responseWrapper().raw(), serializerChain, context.httpRequest());
        } else if (chain != null) {
            chain.doFilter(context.httpRequest(), context.responseWrapper().raw());
        }
    }

    private void checkBodyResult(RouteContext context) {

        // If redirected and content is null set to empty string to not throw NotConsumedException
        if (context.body().notSet() && context.responseWrapper().isRedirected()) {
            context.body().set("");
        }

        if (context.body().notSet() && hasOtherHandlers) {
            if (context.httpRequest() instanceof HttpRequestWrapper) {
                ((HttpRequestWrapper) context.httpRequest()).notConsumed(true);
                return;
            }
        }

        if (context.body().notSet()) {
            LOG.info("The requested route [{}] has not been mapped in Spark for {}: [{}]",
                context.uri(), ACCEPT_TYPE_REQUEST_MIME_HEADER, context.acceptType());
            context.responseWrapper().raw().setStatus(HttpServletResponse.SC_NOT_FOUND);

            if (CustomErrorPages.existsFor(404)) {
                context.requestWrapper().setDelegate(RequestResponseFactory.create(context.httpRequest()));
                context.responseWrapper().setDelegate(RequestResponseFactory.create(context.responseWrapper().raw()));
                context.body().set(CustomErrorPages.getFor(404, context.requestWrapper(), context.responseWrapper()));
            } else {
                context.body().set(String.format(CustomErrorPages.NOT_FOUND));
            }
        }
    }

    private void afterAfterFilter(RouteContext context) {
        try {
            AfterAfterFilters.execute(context);
        } catch (Exception generalException) {
            GeneralError.modify(
                context.httpRequest(),
                context.responseWrapper().raw(),
                context.body(),
                context.requestWrapper(),
                context.responseWrapper(),
                generalException);
        }
    }

    private String getHttpMethodFrom(HttpServletRequest httpRequest) {
        String method = httpRequest.getHeader(HTTP_METHOD_OVERRIDE_HEADER);

        if (method == null) {
            method = httpRequest.getMethod();
        }
        return method;
    }

    @Override
    public void destroy() {
    }


}
