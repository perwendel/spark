package spark.resource;

import org.junit.Assert;
import org.junit.Test;


public class ClassPathResourceTest {

    @Test
    public void testInvalidPath() throws Exception {
        try{
            ClassPathResource classPathResource = new ClassPathResource("../", null);
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertEquals("Path is not valid", e.getMessage());
        }
    }

    @Test
    public void testValidPath() throws Exception {
        try{
            ClassPathResource classPathResource1 = new ClassPathResource("/hi/there/this/is/valid", null);
            ClassPathResource classPathResource2 = new ClassPathResource("/relative/path:/here", null);
        } catch (IllegalArgumentException e){
            Assert.fail();
        }
    }
}
