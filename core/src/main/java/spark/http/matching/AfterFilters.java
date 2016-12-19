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

import java.util.List;

import spark.FilterImpl;
import spark.Request;
import spark.RequestResponseFactory;
import spark.route.HttpMethod;
import spark.routematch.RouteMatch;

/**
 * Executes the after filters matching an HTTP request.
 */
final class AfterFilters {

    static void execute(RouteContext context) throws Exception {

        Object content = context.body().get();

        List<RouteMatch> matchSet = context.routeMatcher().findMultiple(HttpMethod.after,
                                                                        context.uri(),
                                                                        context.acceptType());

        for (RouteMatch filterMatch : matchSet) {
            Object filterTarget = filterMatch.getTarget();

            if (filterTarget instanceof FilterImpl) {

                if (context.requestWrapper().getDelegate() == null) {
                    Request request = RequestResponseFactory.create(filterMatch, context.httpRequest());
                    context.requestWrapper().setDelegate(request);
                } else {
                    context.requestWrapper().changeMatch(filterMatch);
                }

                context.responseWrapper().setDelegate(context.response());

                FilterImpl filter = (FilterImpl) filterTarget;
                filter.handle(context.requestWrapper(), context.responseWrapper());

                String bodyAfterFilter = context.response().body();

                if (bodyAfterFilter != null) {
                    content = bodyAfterFilter;
                }
            }
        }

        context.body().set(content);
    }

}
