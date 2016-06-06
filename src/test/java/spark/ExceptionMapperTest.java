package spark;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.examples.exception.NotFoundException;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class ExceptionMapperTest {
	
	private static final int HTTP_404 = 404;
	private static final String THROW_EXCEPTION_PATH = "/throwexception";
	
	private static final String NOT_FOUND_EXCEPTION_FROM_SERVICE1 = "Not found exception from service 1";
	private static final String NOT_FOUND_EXCEPTION_FROM_SERVICE2 = "Not found exception from service 2";
	
	private static Service service1;
	private static Service service2;
	
	private static SparkTestUtil testUtilService1;
	private static SparkTestUtil testUtilService2;
	
	@BeforeClass
	public static void setup() throws IOException {
		
		testUtilService1 = new SparkTestUtil(4567);
		
		testUtilService2 = new SparkTestUtil(4568);
		
		service1 = Service.ignite();
		service1.port(4567);

		service2 = Service.ignite();
		service2.port(4568);
		
    	service1.get(THROW_EXCEPTION_PATH, (q, a) -> {
            throw new NotFoundException();
        });
    	
    	service1.exception(NotFoundException.class, (exception, q, a) -> {
            a.status(HTTP_404);
            a.body(NOT_FOUND_EXCEPTION_FROM_SERVICE1);
        });    	
    	
    	service2.get(THROW_EXCEPTION_PATH, (q, a) -> {
            throw new NotFoundException();
        });
    	
    	service2.exception(NotFoundException.class, (exception, q, a) -> {
            a.status(HTTP_404);
            a.body(NOT_FOUND_EXCEPTION_FROM_SERVICE2);
        });
    	
    	service1.awaitInitialization();
    	service2.awaitInitialization();
	}
	
    @AfterClass
    public static void tearDown() {
        service1.stop();
        service2.stop();
    }	

    @Test
    public void testService1Exception() throws Exception {
    	System.out.println("Executing service 1 test");
        UrlResponse response = testUtilService1.doMethod("GET", THROW_EXCEPTION_PATH, null);
        Assert.assertEquals(HTTP_404, response.status);
        Assert.assertEquals(NOT_FOUND_EXCEPTION_FROM_SERVICE1, response.body);    	
    }
    
    @Test
    public void testService2Exception() throws Exception {
    	System.out.println("Executing service 2 test");
        UrlResponse response = testUtilService2.doMethod("GET", THROW_EXCEPTION_PATH, null);
        Assert.assertEquals(HTTP_404, response.status);
        Assert.assertEquals(NOT_FOUND_EXCEPTION_FROM_SERVICE2, response.body);    	
    }
    
}