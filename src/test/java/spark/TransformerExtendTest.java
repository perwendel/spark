package spark;

import org.junit.Before;
import org.junit.Test;
import spark.examples.transformer.MyMessage;
import spark.routematch.RouteMatch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//CS304 Issue link: https://github.com/perwendel/spark/issues/534
public class TransformerExtendTest {

    HttpServletRequest servletRequest;
    HttpServletResponse servletResponse;
    Request request;
    Response response;
    RouteMatch match = new RouteMatch(null, "/hi", "/hi", "text/html");
    TransformerExtend transformer;

    @Before
    public void setup() {
        servletRequest = mock(HttpServletRequest.class);
        request = new Request(match, servletRequest);


        servletResponse = mock(HttpServletResponse.class);
        response = new Response(servletResponse);

        transformer = new TransformerExtend();
    }

    @Test
    public void testJsonRequest() throws Exception {
        final String contentType = "application/json";

        when(servletRequest.getContentType()).thenReturn(contentType);

        assertEquals("{\"message\":\"Hello World\"}", transformer.render(new MyMessage("Hello World"), request, response));

    }

    @Test
    public void testNotJsonRequest() throws Exception {
        final String contentType = "text/xml";

        when(servletRequest.getContentType()).thenReturn(contentType);

        assertEquals("Not Json type message. Other types are not implemented.", transformer.render(new MyMessage("Hello World"), request, response));

    }
}
