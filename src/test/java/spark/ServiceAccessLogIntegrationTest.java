package spark;

import org.eclipse.jetty.server.NCSARequestLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.accesslog.AccessLogger;
import spark.util.SparkTestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Service.ignite;

public class ServiceAccessLogIntegrationTest {

    public static final String TARGET_JETTY_REQUEST_LOG = "./target/jetty.request.log";
    public static final String HELLO_WORLD = "Hello World!";
    private static Service service;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAccessLogIntegrationTest.class);
    private static final File myLogFile = new File(TARGET_JETTY_REQUEST_LOG);

    @BeforeClass
    public static void setUpClass() throws Exception {

        myLogFile.delete();
        assertFalse(myLogFile.exists());

        service = ignite();
        service.port(0);
        service.accessLogger(new AccessLogger(of(getNcsaRequestLog())));
        service.get("/hi", (q, a) -> HELLO_WORLD);

        service.awaitInitialization();
    }

    @Test
    public void testAccessLogs_withFile() throws Exception {
        SparkTestUtil testUtil = new SparkTestUtil(service.port());
        LOGGER.info("Should log in file ->" + myLogFile.getAbsolutePath());
        for (int i = 1; i <= 10; i++) {
            accessService(testUtil);
            verifyLogEntry(i);
        }
    }

    private void verifyLogEntry(int expectedLines) throws IOException {
        List<String> logLines = Files.lines(myLogFile.toPath()).collect(toList());
        assertFalse(logLines.isEmpty());
        assertEquals(expectedLines, logLines.size());
        assertTrue(logLines.stream().allMatch(l -> l.contains("GET") && l.contains("200") && l.contains(HELLO_WORLD.length() + "")));
    }

    private void accessService(SparkTestUtil testUtil) throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        assertEquals(200, response.status);
        assertEquals("Hello World!", response.body);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
        myLogFile.delete();
    }

    static NCSARequestLog getNcsaRequestLog() {
        NCSARequestLog requestLog = new NCSARequestLog(TARGET_JETTY_REQUEST_LOG);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);
        return requestLog;
    }
}
