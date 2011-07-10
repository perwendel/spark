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
package spark.webserver;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.Access;
import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.Route;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;

/**
 * Filter for matching of filters and routes.
 * 
 * @author Per Wendel
 */
public class MatcherFilter implements Filter, HttpErrorCodes {
	private RouteMatcher routeMatcher;
	private boolean isServletContext;

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(MatcherFilter.class);

	/**
	 * Constructor
	 *
	 * @param routeMatcher
	 *            The route matcher
	 * @param isServletContext
	 *            If true, chain.doFilter will be invoked if request is not
	 *            consumed by Spark.
	 */
	public MatcherFilter(RouteMatcher routeMatcher, boolean isServletContext) {
		this.routeMatcher = routeMatcher;
		this.isServletContext = isServletContext;
	}

	public void init(FilterConfig filterConfig) {
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

		String httpMethodStr = httpRequest.getMethod().toLowerCase();
		HttpMethod httpMethod = HttpMethod.valueOf(httpMethodStr);
		String uri = httpRequest.getRequestURI().toLowerCase();

		String bodyContent = null;

		LOG.debug("httpMethod:" + httpMethodStr + ", uri: " + uri);
		try {
			long t0 = System.currentTimeMillis();

			// BEFORE filters
			List<RouteMatch> matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.before, uri);
			bodyContent = processFilters(matchSet, httpRequest, httpResponse);

			bodyContent = processRequest(httpRequest, httpResponse, httpMethodStr, httpMethod, uri, bodyContent);

			// AFTER filters
			matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.after, uri);
			String bodyAfterFilter = processFilters(matchSet, httpRequest, httpResponse);
			if (bodyAfterFilter != null) {
				bodyContent = bodyAfterFilter;
			}

			long t1 = System.currentTimeMillis() - t0;
			LOG.debug("Time for request: " + t1);
		} catch (HaltException hEx) {
			LOG.debug("halt performed");
			httpResponse.setStatus(hEx.getStatusCode());
			if (hEx.getBody() != null) {
				bodyContent = hEx.getBody();
			} else {
				bodyContent = "";
			}
		}

		boolean consumed = bodyContent != null ? true : false;

		if (!(consumed || isServletContext)) {
			httpResponse.setStatus(HttpErrorCodes.NOT_FOUND);
			bodyContent = NOT_FOUND;
			consumed = true;
		}

		if (consumed) {
			// Write body content
			httpResponse.getOutputStream().write(bodyContent.getBytes("utf-8"));
		} else if (chain != null) {
			chain.doFilter(httpRequest, httpResponse);
		}
	}

	private String processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, String httpMethodStr,
			HttpMethod httpMethod, String uri, String bodyContent) {

		String result = bodyContent;

		RouteMatch match = null;
		match = routeMatcher.findTargetForRequestedRoute(
				HttpMethod.valueOf(httpMethodStr), uri);

		Object target = null;
		if (match != null) {
			target = match.getTarget();
		} else if (httpMethod == HttpMethod.head && bodyContent == null) {
			// See if get is mapped to provide default head mapping
			result = routeMatcher.findTargetForRequestedRoute(HttpMethod.get, uri) != null ? "" : null;
		}

		if (target != null) {
			try {
				Object routeProcessingResult = null;
				if (target instanceof Route) {
					Route route = ((Route) target);
					Request request = RequestResponseFactory.create(match, httpRequest);
					Response response = RequestResponseFactory.create(httpResponse);

					RequestWrapper req = new RequestWrapper(request);
					ResponseWrapper res = new ResponseWrapper(response);

					routeProcessingResult = route.handle(req, res);
				}
				if (routeProcessingResult != null) {
					result = routeProcessingResult.toString();
				}
			} catch (HaltException hEx) {
				throw hEx;
			} catch (Exception e) {
				LOG.error(e);
				httpResponse.setStatus(HttpErrorCodes.INTERNAL_SERVER_ERROR);
				result = INTERNAL_ERROR;
			}
		}
		return result;
	}

	private String processFilters(List<RouteMatch> matchSet,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		for (RouteMatch filterMatch : matchSet) {
			Object filterTarget = filterMatch.getTarget();
			if (isFilter(filterTarget)) {
				Request request = RequestResponseFactory.create(filterMatch, httpRequest);
				Response response = RequestResponseFactory.create(httpResponse);

				spark.Filter filter = (spark.Filter) filterTarget;

				RequestWrapper req = new RequestWrapper(request);
				ResponseWrapper res = new ResponseWrapper(response);

				filter.handle(req, res);

				String bodyAfterFilter = Access.getBody(response);
				return bodyAfterFilter;
			}
		}
		return null;
	}

	private boolean isFilter(Object filterTarget) {
		return filterTarget != null && filterTarget instanceof spark.Filter;
	}

	public void destroy() {
	}

	private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>The requested route has not been mapped in Spark</body></html>";
	private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
}
