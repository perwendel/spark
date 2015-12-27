package spark;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilterImplTest {

    public String PATH_TEST;
    public String ACCEPT_TYPE_TEST;

    public FilterImpl filter;

    @Before
    public void setup(){
        PATH_TEST = "/etc/test";
        ACCEPT_TYPE_TEST  = "test/*";
    }

    @Test
    public void testConstructor(){
        FilterImpl filter = new FilterImpl(PATH_TEST, ACCEPT_TYPE_TEST) {
            @Override
            public void handle(Request request, Response response) throws Exception {
            }
        };
        assertEquals("Path is not equal", PATH_TEST, filter.getPath());
        assertEquals("Accept type is not equal", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testGets() throws Exception {
        filter = FilterImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        assertEquals("Path is not equal", PATH_TEST, filter.getPath());
        assertEquals("Accept type is not equal", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testCreate_withOutAssignAcceptTypeInTheParameters(){
        filter = FilterImpl.create(PATH_TEST, null);
        assertEquals("Path is not equal", PATH_TEST, filter.getPath());
        assertEquals("Accept type is not equal", RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }

    @Test
    public void testCreate_withAcceptTypeNullValueInTheParameters(){
        filter = FilterImpl.create(PATH_TEST, null, null);
        assertEquals("Path is not equal", PATH_TEST, filter.getPath());
        assertEquals("Accept type is not equal", RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }
}