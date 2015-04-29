package spark.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.BeforeClass;

public class ServletTest extends BaseServletTest {

    @BeforeClass
    public static void setup() {
        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath(BaseServletTest.SOMEPATH);
        bb.setWar("src/test/webapp");
        bb.addServlet(SparkServlet.class, "/*").setInitParameter(SparkHandler.APPLICATION_CLASS_PARAM, TEST_APP_CLASS);
        BaseServletTest.setup(bb);
    }

}
