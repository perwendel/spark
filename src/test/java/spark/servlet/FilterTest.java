package spark.servlet;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.BeforeClass;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by andrey.vorobyov on 3/16/15.
 */
public class FilterTest extends BaseServletTest {

    @BeforeClass
    public static void setup(){
        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath(BaseServletTest.SOMEPATH);
        bb.setWar("src/test/webapp");
        bb.addFilter(SparkFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST)).setInitParameter(SparkHandler.APPLICATION_CLASS_PARAM, TEST_APP_CLASS);
        BaseServletTest.setup(bb);
    }
}
