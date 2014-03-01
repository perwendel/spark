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
package spark;

/**
 * A Route is built up by a path (for url-matching) and the implementation of
 * the 'handle' method. When a request is made, if present, the matching routes
 * 'handle' method is invoked. The object that is returned from 'handle' will be
 * set to the response body (toString()).
 * 
 * @author Per Wendel
 */
public abstract class Route extends AbstractRoute {

	private static final String DEFAULT_ACCEPT_TYPE = "*/*";

	private final String path;
	private final String acceptType;
	private final boolean ignoreAfterHandling;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The route path which is used for matching. (e.g. /hello,
	 *            users/:name)
	 */
	protected Route(String path) {
		this(path, Route.DEFAULT_ACCEPT_TYPE);
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The route path which is used for matching. (e.g. /hello,
	 *            users/:name)
	 * @param acceptType
	 *            The accept type which is used for matching.
	 */
	protected Route(String path, String acceptType) {
		this(path, acceptType, false);
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The route path which is used for matching. (e.g. /hello,
	 *            users/:name)
	 * @param ignoreAfterHandling
	 *            Indicate if changes won't be made after the handling.
	 */
	protected Route(String path, boolean ignoreAfterHandling) {
		this(path, Route.DEFAULT_ACCEPT_TYPE, ignoreAfterHandling);
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            The route path which is used for matching. (e.g. /hello,
	 *            users/:name)
	 * @param acceptType
	 *            The accept type which is used for matching.
	 * @param ignoreAfterHandling
	 *            Indicate if changes won't be made after the handling.
	 */
	protected Route(String path, String acceptType, boolean ignoreAfterHandling) {
		this.path = path;
		this.acceptType = acceptType;
		this.ignoreAfterHandling = ignoreAfterHandling;
	}

	/**
	 * Invoked when a request is made on this route's corresponding path e.g.
	 * '/hello'
	 * 
	 * @param request
	 *            The request object providing information about the HTTP
	 *            request
	 * @param response
	 *            The response object providing functionality for modifying the
	 *            response
	 * 
	 * @return The content to be set in the response
	 */
	public abstract Object handle(Request request, Response response);

	/**
	 * This method should render the given element into something that can be
	 * send through Response element. By default this method returns the result
	 * of calling toString method in given element, but can be overridden.
	 * 
	 * @param element
	 *            to be rendered.
	 * @return body content.
	 */
	// TODO change String return type to Stream. It should be done in another
	// issue.
	public String render(Object element) {
		if (element != null)
			return element.toString();
		else
			return null;
	}

	public String getAcceptType() {
		return this.acceptType;
	}

	/**
	 * Indicate if changes will be ignored after calling the
	 * {@link #handle(Request, Response) handle} method.
	 * 
	 * @return <code>true</code> if will ignore, <code>false</code> otherwise.
	 */
	public boolean ignoreAfterHandling() {
		return this.ignoreAfterHandling;
	}

	/**
	 * Returns this route's path
	 */
	String getPath() {
		return this.path;
	}

}
