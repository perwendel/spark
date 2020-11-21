package spark;

import org.junit.Assert;
import org.junit.Test;
import static spark.Spark.*;
import static spark.Spark.init;

public class ServerInformationTest2 {

    @Test
    public void testException() {
        try {
            boolean cert1 = serverNeedClientCert();
            Assert.fail();
        } catch (IllegalStateException e) {

        }
        init();
        boolean cert2 = serverNeedClientCert();
        Assert.assertFalse(cert2);
    }
}
