package spark.resource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jsiebahn
 * @since 06.04.2018
 */
public class ClassPathResourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldPreventReadingFromMetaInf() {

        new ClassPathResource("/META-INF/something-secret.xml");

    }

    @Test
    public void shouldSupportServletSpecResources() {
        ClassPathResource classPathResource = new ClassPathResource(
            "/META-INF/resources/ClassPathResourceTest.css");
        Assert.assertEquals(true, classPathResource.exists());
    }
}
