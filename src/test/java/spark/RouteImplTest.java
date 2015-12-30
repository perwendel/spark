package spark;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RouteImplTest {

    public String PATH_TEST;
    public String ACCEPT_TYPE_TEST;

    public RouteImpl route;

    @Before
    public void setup(){
        PATH_TEST = "/opt/test";
        ACCEPT_TYPE_TEST  = "*/test";
    }

    @Test
    public void testConstructor(){
        RouteImpl route = new RouteImpl(PATH_TEST) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return null;
            }
        };
        assertEquals("Should return path specified", PATH_TEST, route.getPath());
    }

    @Test
    public void testGets_thenReturnGetPathAndGetAcceptTypeSuccessfully() throws Exception {
        route = RouteImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        assertEquals("Should return path specified", PATH_TEST, route.getPath());
        assertEquals("Should return accept type specified", ACCEPT_TYPE_TEST, route.getAcceptType());
    }

    @Test
    public void testHandle_thenReturnObjectValid() throws Exception {
        Route routeMock = EasyMock.createMock(Route.class);
        RouteImpl route = RouteImpl.create(PATH_TEST, routeMock);

        EasyMock.expect(route.handle(null, null)).andReturn(new Object());

        EasyMock.replay(routeMock);
        Object value = route.handle(null, null);
        EasyMock.verify(routeMock);

        assertNotNull("Should return null because the request and response from handle are null", value);
    }

    @Test
    public void testCreate_whenOutAssignAcceptTypeInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        route = RouteImpl.create(PATH_TEST, null);
        assertEquals("Should return path specified", PATH_TEST, route.getPath());
        assertEquals("Should return accept type specified", RouteImpl.DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testCreate_whenAcceptTypeNullValueInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        route = RouteImpl.create(PATH_TEST, null, null);
        assertEquals("Should return path specified", PATH_TEST, route.getPath());
        assertEquals("Should return accept type specified", RouteImpl.DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testRender_whenElementParameterValid_thenReturnValidObject() throws Exception {
        String finalObjValue = "object_value";
        route = RouteImpl.create(PATH_TEST, null);
        Object value = route.render(finalObjValue);
        assertNotNull("Should return an Object because we configured it to have one", value);
        assertEquals("Should return a string object specified", finalObjValue, value.toString());
    }

    @Test
    public void testRender_whenElementParameterIsNull_thenReturnNull() throws Exception {
        route = RouteImpl.create(PATH_TEST, null);
        Object value = route.render(null);
        assertNull("Should return null because the element from render is null", value);
    }
}