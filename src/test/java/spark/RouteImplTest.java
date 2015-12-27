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
        assertEquals("Path is not equal", PATH_TEST, route.getPath());
    }

    @Test
    public void testGets() throws Exception {
        route = RouteImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        assertEquals("Path is not equal", PATH_TEST, route.getPath());
        assertEquals("Accept type is not equal", ACCEPT_TYPE_TEST, route.getAcceptType());
    }

    @Test
    public void testHandle() throws Exception {
        Route routeMock = EasyMock.createMock(Route.class);
        RouteImpl route = RouteImpl.create(PATH_TEST, routeMock);

        EasyMock.expect(route.handle(null, null)).andReturn(new Object());

        EasyMock.replay(routeMock);
        Object value = route.handle(null, null);
        EasyMock.verify(routeMock);

        assertNotNull("Value is null", value);
    }

    @Test
    public void testCreate_withOutAssignAcceptTypeInTheParameters(){
        route = RouteImpl.create(PATH_TEST, null);
        assertEquals("Path is not equal", PATH_TEST, route.getPath());
        assertEquals("Accept type is not equal", RouteImpl.DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testCreate_withAcceptTypeNullValueInTheParameters(){
        route = RouteImpl.create(PATH_TEST, null, null);
        assertEquals("Path is not equal", PATH_TEST, route.getPath());
        assertEquals("Accept type is not equal", RouteImpl.DEFAULT_ACCEPT_TYPE, route.getAcceptType());
    }

    @Test
    public void testRender_withElementParameterValid_thenReturnValidObject() throws Exception {
        route = RouteImpl.create(PATH_TEST, null);
        Object value = route.render("object_value");
        assertNotNull("Value is null", value);
        assertEquals("Value is not equal", "object_value", value.toString());
    }

    @Test
    public void testRender_withElementParameterIsNull_thenReturnNull() throws Exception {
        route = RouteImpl.create(PATH_TEST, null);
        Object value = route.render(null);
        assertNull("Value is not null", value);
    }
}