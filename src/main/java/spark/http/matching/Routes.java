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

/**
 * Created by Per Wendel on 2016-01-28.
 */
final class Routes {

    static void execute(RouteContext context) throws Exception {

        Object content = context.body().get();

        RouteMatch match = context.routeMatcher().find(context.httpMethod(), context.uri(), context.acceptType());

        Object target = null;
        if (match != null) {
            target = match.getTarget();
        } else if (context.httpMethod() == HttpMethod.head && context.body().notSet()) {
            // See if get is mapped to provide default head mapping
            content =
                    context.routeMatcher().find(HttpMethod.get, context.uri(), context.acceptType())
                            != null ? "" : null;
        }

        if (target != null) {
            Object result = null;

            if (target instanceof RouteImpl) {
                RouteImpl route = ((RouteImpl) target);

                if (context.requestWrapper().getDelegate() == null) {
                    Request request = RequestResponseFactory.create(match, context.httpRequest());
                    context.requestWrapper().setDelegate(request);
                } else {
                    context.requestWrapper().changeMatch(match);
                }

                context.responseWrapper().setDelegate(context.response());

                Object element = route.handle(context.requestWrapper(), context.responseWrapper());
                result = route.render(element);
            }

            if (result != null) {
                content = result;

                if (content instanceof String) {
                    String contentStr = (String) content;

                    if (!contentStr.equals("")) {
                        context.responseWrapper().body(contentStr);
                    }
                }
            }
        }

        context.body().set(content);
    }

}
