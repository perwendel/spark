package spark.route;

import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import static org.junit.Assert.*;

public class SimpleRouteMatcherTest {

	@Test
	public void testParseValidateAddRoute_whenHttpMethodIsValid_thenAddRoute() {
		String route = "get'/hello'";
		String acceptType="*/*";
		Object target = new Object(); 
		
		RouteEntry expectedRouteEntry = new RouteEntry();
		expectedRouteEntry.acceptedType = acceptType;
		expectedRouteEntry.httpMethod = HttpMethod.get;
		expectedRouteEntry.path = "/hello";
		expectedRouteEntry.target = target;
		
		SimpleRouteMatcher simpleRouteMatcher = new SimpleRouteMatcher();
		simpleRouteMatcher.parseValidateAddRoute(route, acceptType, target);
		
		List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
	    assertEquals(routes.size(),1);
	    assertEquals(routes.get(0).acceptedType,expectedRouteEntry.acceptedType);
	    assertEquals(routes.get(0).httpMethod,expectedRouteEntry.httpMethod);
	    assertEquals(routes.get(0).path,expectedRouteEntry.path);
	    assertEquals(routes.get(0).target,expectedRouteEntry.target);
	}
	
	@Test
	public void testParseValidateAddRoute_whenHttpMethodIsInvalid_thenDoNotAddRoute() {
		String route = "test'/hello'";
		String acceptType="*/*";
		Object target = new Object(); 
		
		SimpleRouteMatcher simpleRouteMatcher = new SimpleRouteMatcher();
		simpleRouteMatcher.parseValidateAddRoute(route, acceptType, target);
		
		List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
	    assertEquals(routes.size(),0);
	}
}
