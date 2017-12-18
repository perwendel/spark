/*
 * Copyright 2016 - Per Wendel
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

import spark.Request;
import spark.RequestResponseFactory;
import spark.RouteImpl;
import spark.route.HttpMethod;
import spark.routematch.RouteMatch;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Per Wendel on 2016-01-28.
 */
final class Routes {

    static Optional<CompletableFuture> execute(RouteContext context) throws Exception {

        RouteMatch match = context.routeMatcher().find(context.httpMethod(), context.uri(), context.acceptType());

        if(match != null){
            return handleRouteMatch(context, match);
        } else if (context.httpMethod() == HttpMethod.head && context.body().notSet()) {
            // See if get is mapped to provide default head mapping
            context.body().set(defaultHeadMapping(context));
        }
        return Optional.empty();
    }

    private static Optional<CompletableFuture> handleRouteMatch(RouteContext context, RouteMatch match) throws Exception {
        Object target = match.getTarget();

        if (target instanceof RouteImpl) {
            RouteImpl route = ((RouteImpl) target);
            handleDelegate(context, match);

            Object element = route.handle(context.requestWrapper(), context.responseWrapper());
            if(element instanceof CompletableFuture){
                CompletableFuture future = (CompletableFuture)element;
                return Optional.of(future.thenAccept(futureElement -> {
                    System.out.println("future el: "+futureElement);
                    try {
                        setBodyFromResult(context, route, futureElement);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }));
            } else {
                setBodyFromResult(context, route, element);
            }
        }
        return Optional.empty();
    }

    private static void handleDelegate(RouteContext context, RouteMatch match) {
        if (context.requestWrapper().getDelegate() == null) {
            Request request = RequestResponseFactory.create(match, context.httpRequest());
            context.requestWrapper().setDelegate(request);
        } else {
            context.requestWrapper().changeMatch(match);
        }

        context.responseWrapper().setDelegate(context.response());
    }

    private static void setBodyFromResult(RouteContext context, RouteImpl route, Object element) throws Exception {
        if (!context.responseWrapper().isRedirected() && element!=null) {
            Object result = route.render(element);
            if (result != null) {
                Object content = result;

                if (content instanceof String) {
                    String contentStr = (String) content;

                    if (!contentStr.equals("")) {
                        context.responseWrapper().body(contentStr);
                    }
                }
                context.body().set(content);
            }
        }
    }

    private static Object defaultHeadMapping(RouteContext context) {
        return context.routeMatcher().find(HttpMethod.get, context.uri(), context.acceptType())
            != null ? "" : null;
    }

}
