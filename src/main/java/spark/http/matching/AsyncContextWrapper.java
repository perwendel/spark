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

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import spark.serialization.SerializerChain;

/**
 * This class wraps the AsyncContext and adds support for calling After and AfterAfter filters
 * when the async request is complete.
 *
 * @author : Steve Hurlbut
 *
 */
public class AsyncContextWrapper implements AsyncContext {
    private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AsyncContextWrapper.class);
    private AsyncContext wrappedContext;
    private RouteContext routeContext;
    private SerializerChain serializerChain;

    public AsyncContextWrapper(RouteContext routeContext,
                               SerializerChain serializerChain) {
        this.routeContext = routeContext;
        this.serializerChain = serializerChain;
    }

    /**
     * Adds an AysncContext listener that handles timeout events.
     * @param context the wrapped context
     */
    public void setWrappedContext(AsyncContext context) {
        wrappedContext = context;
        AsyncListener listener = new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                processException(new IOException("Asynchronous request timeout expired"));
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
            }
        };
        context.addListener(listener);
    }


    @Override
    public ServletRequest getRequest() {
        return wrappedContext.getRequest();
    }

    @Override
    public ServletResponse getResponse() {
        return wrappedContext.getResponse();
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return wrappedContext.hasOriginalRequestAndResponse();
    }

    @Override
    public void dispatch() {
        wrappedContext.dispatch();
    }

    @Override
    public void dispatch(String s) {
        wrappedContext.dispatch(s);
    }

    @Override
    public void dispatch(ServletContext servletContext, String s) {
        wrappedContext.dispatch(servletContext, s);

    }

    /**
     * When the async context is complete, call the After and AfterAfter filters and process
     * the response body.
     */
    @Override
    public void complete() {
        try {
            try {
                AfterFilters.execute(routeContext);
                AfterAfterFilters.execute(routeContext);
                if (routeContext.response().body() != null) {
                    routeContext.body().set(routeContext.response().body());
                }
                if (routeContext.body().isSet()) {
                    routeContext.body()
                        .serializeTo(routeContext.responseWrapper().raw(), serializerChain, routeContext.httpRequest());
                } else {
                    routeContext.responseWrapper().raw().getOutputStream().close();
                }
            }
            catch (Exception ex) {
                processException(ex);
            }
        }
        catch (IOException e) {
            LOG.error("Error completing async request", e);
        }
        finally {
            wrappedContext.complete();
        }
    }

    @Override
    public void start(Runnable runnable) {
        wrappedContext.start(runnable);
    }

    @Override
    public void addListener(AsyncListener asyncListener) {
        wrappedContext.addListener(asyncListener);
    }

    @Override
    public void addListener(AsyncListener asyncListener, ServletRequest servletRequest,
                            ServletResponse servletResponse) {
        wrappedContext.addListener(asyncListener, servletRequest, servletResponse);
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> aClass) throws ServletException {
        return wrappedContext.createListener(aClass);
    }

    @Override
    public void setTimeout(long l) {
        wrappedContext.setTimeout(l);

    }

    @Override
    public long getTimeout() {
        return wrappedContext.getTimeout();
    }

    private void processException(Exception exception) throws IOException {
        GeneralError.modify(
            routeContext.httpRequest(),
            routeContext.responseWrapper().raw(),
            routeContext.body(),
            routeContext.requestWrapper(),
            routeContext.responseWrapper(),
            exception);
        try {
            AfterAfterFilters.execute(routeContext);
        }
        catch (Exception e) {
            LOG.error("Error processing async request", e);
        }
        if (routeContext.body().isSet()) {
            routeContext.body().serializeTo(routeContext.responseWrapper().raw(), serializerChain, routeContext.httpRequest());
        } else {
            routeContext.responseWrapper().raw().getOutputStream().close();
        }
    }
}
