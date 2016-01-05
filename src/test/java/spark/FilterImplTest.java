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
        assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        assertEquals("Should return accept type specified", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testGets_thenReturnGetPathAndGetAcceptTypeSuccessfully() throws Exception {
        filter = FilterImpl.create(PATH_TEST, ACCEPT_TYPE_TEST, null);
        assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        assertEquals("Should return accept type specified", ACCEPT_TYPE_TEST, filter.getAcceptType());
    }

    @Test
    public void testCreate_whenOutAssignAcceptTypeInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        filter = FilterImpl.create(PATH_TEST, null);
        assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        assertEquals("Should return accept type specified", RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }

    @Test
    public void testCreate_whenAcceptTypeNullValueInTheParameters_thenReturnPathAndAcceptTypeSuccessfully(){
        filter = FilterImpl.create(PATH_TEST, null, null);
        assertEquals("Should return path specified", PATH_TEST, filter.getPath());
        assertEquals("Should return accept type specified", RouteImpl.DEFAULT_ACCEPT_TYPE, filter.getAcceptType());
    }
}