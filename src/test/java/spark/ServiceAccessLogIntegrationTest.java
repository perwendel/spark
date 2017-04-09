package spark;

import org.eclipse.jetty.server.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.accesslog.AccessLogger;
import spark.util.SparkTestUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spark.Service.ignite;

public class ServiceAccessLogIntegrationTest {

    public static final String TARGET_JETTY_REQUEST_LOG = "./target/jetty.request.log";
    public static final String HELLO_WORLD = "Hello World!";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Service service;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAccessLogIntegrationTest.class);
    private static final File myLogFile = new File(TARGET_JETTY_REQUEST_LOG);
    private static StringWriter writer;

    @BeforeClass
    public static void setUpClass() throws Exception {

        myLogFile.delete();
        assertFalse(myLogFile.exists());

        service = ignite();
        service.port(0);
        writer = new StringWriter();
        service.accessLogger(new AccessLogger(of(getNcsaRequestLog(writer))));
        service.get("/hi", (q, a) -> HELLO_WORLD);
        service.awaitInitialization();
    }

    @Test
    public void testAccessLogs_withFile() throws Exception {
        SparkTestUtil testUtil = new SparkTestUtil(service.port());
        LOGGER.info("Should log in file ->" + myLogFile.getAbsolutePath());
        for (int i = 1; i <= 10; i++) {
            accessService(testUtil);
        }
    }

    static void verifyLog() {
        List<String> logLines = asList(writer.toString().split(LINE_SEPARATOR));
        assertFalse(logLines.isEmpty());
        assertEquals(10, logLines.size());
        assertTrue(logLines.stream().allMatch(l -> l.contains("GET") && l.contains("200") && l.contains(HELLO_WORLD.length() + "")));
        LOGGER.info(String.format("The sample log is ->%s%s", LINE_SEPARATOR, logLines.stream().collect(Collectors.joining(LINE_SEPARATOR))));
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
        //unusual place, I know, but we need to make sure that the logs are completely flushed/written
        verifyLog();
    }

    static NCSARequestLog getNcsaRequestLog(Writer w) {
        NCSARequestLog requestLog = new NCSARequestLog(TARGET_JETTY_REQUEST_LOG) {
            @Override
            public void write(String requestEntry) throws IOException {
                w.append(String.format("%s%s", requestEntry, LINE_SEPARATOR));
                super.write(requestEntry);
            }
        };
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);
        return requestLog;
    }
}
