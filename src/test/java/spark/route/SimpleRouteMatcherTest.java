package spark.route;

import java.util.ArrayList;
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
		List<RouteEntry> expectedRoutes = new ArrayList<>();
		expectedRoutes.add(expectedRouteEntry);
		
		SimpleRouteMatcher simpleRouteMatcher = new SimpleRouteMatcher();
		simpleRouteMatcher.parseValidateAddRoute(route, acceptType, target);
		
		List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
		assertTrue("Should return true because http method is valid and the route should be added to the list",
				    equalsRouteEntryList(routes, expectedRoutes));
	    
	}
	
	private boolean equalsRouteEntryList(List<RouteEntry> routes, List<RouteEntry> expectedRoutes) {
		if(routes.size() != expectedRoutes.size()) {
			return false;
		}
		for(int i=0;i < routes.size();i++) {
			if(!routes.get(i).acceptedType.equals(expectedRoutes.get(i).acceptedType)) {
				return false;
			}
			if(!routes.get(i).httpMethod.equals(expectedRoutes.get(i).httpMethod)) {
				return false;
			}
			if(!routes.get(i).path.equals(expectedRoutes.get(i).path)) {
				return false;
			}
			if(!routes.get(i).target.equals(expectedRoutes.get(i).target)) {
				return false;
			}
		}
	    return true;
	}
	
	@Test
	public void testParseValidateAddRoute_whenHttpMethodIsInvalid_thenDoNotAddRoute() {
		String route = "test'/hello'";
		String acceptType="*/*";
		Object target = new Object(); 
		
		SimpleRouteMatcher simpleRouteMatcher = new SimpleRouteMatcher();
		simpleRouteMatcher.parseValidateAddRoute(route, acceptType, target);
		
		List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
	    assertEquals("Should return 0 because test is not a valid http method, so the route is not added to the list", 
	    		      routes.size(),0);
	}
}
