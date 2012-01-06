package net.wendal.spark.esay;

import static spark.Spark.*;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.*;

/**
 * Map class method as route<p/>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class Routes {
	
	private static final Logger log = LoggerFactory.getLogger(Routes.class);

	/**
	 * Map Classes to Route
	 * @param klasses
	 */
	public static final void make(Class<?>... klasses) {
		if (klasses == null || klasses.length == 0)
			return;
		for (Class<?> klass : klasses)
			make(klass, true);
	}

	/**
	 * Map Class to Route
	 * @param klass your Class
	 * @param singleton all method use the same obj , or not
	 */
	public static final void make(Class<?> klass, boolean singleton) {
		if (klass == null)
			return;
		make(klass, singleton, null);
	}

	/**
	 * Map Object to Route
	 * @param objs your hanlder objs
	 */
	public static void make(Object ...objs) {
		if (objs == null || objs.length == 0)
			return;
		for (Object obj : objs)
			make(obj.getClass(), true, obj);
	}

	/**
	 * Add a method as path and httpMethod as you wish
	 * @param path URL path
	 * @param httpMethod GET/POST etc
	 * @param method java method
	 * @param obj which handle the req
	 */
	public static void make(String path, String httpMethod, Method method, Object obj) {
		addRoute(httpMethod.toLowerCase(), new RouteImpl(path, method, obj));
	}

	/**
	 * Get Spark Request object inside/outside the route method
	 * @return current Request obj
	 */
	public static final Request req() {
		return req.get();
	}

	/**
	 * Get Spark Response object inside/outside the route method
	 * @return current Response obj
	 */
	public static final Response resp() {
		return resp.get();
	}
	
	protected static void make(Class<?> klass, boolean singleton, Object obj) {
		if (obj != null)
			singleton = true;
		if (log.isDebugEnabled())
			log.debug("Map {} , singleton={}", klass, singleton);
		Method[] methods = klass.getMethods();
		for (final Method method : methods) {
			URL url = method.getAnnotation(URL.class);
			if (url == null)
				continue;
			if (!singleton || obj == null)
				try {
					obj = klass.newInstance();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			for (String routePath : url.value())
				for (String type : url.type().split(","))
					make(routePath, type, method, obj);
		}
	}
	
	protected static class RouteImpl extends Route {

		public Object handle(Request request, Response response) {
			req.set(request);
			resp.set(response);
			try {
				if (method.getParameterTypes().length == 0)
					return method.invoke(obj);
				else 
					return method.invoke(obj, request, response);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				req.set(null);
				resp.set(null);
			}
		}
		private Method method;

		private Object obj;

		protected RouteImpl(String path, Method method, Object obj) {
			super(path);
			this.method = method;
			this.obj = obj;
		}
	}
	
	protected static final ThreadLocal<Request> req = new ThreadLocal<Request>();

	protected static final ThreadLocal<Response> resp = new ThreadLocal<Response>();
}
