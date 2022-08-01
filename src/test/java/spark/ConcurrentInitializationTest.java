package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static spark.Spark.get;

public class ConcurrentInitializationTest {

    private static final int NUM_HANDLERS = 10000;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentInitializationTest.class);

    private volatile boolean isClientRunning = true;


    /**
     * Simulates the client which check the server health check awaiting for it to be up and running,
     * for example k8s or other orchestrator or load balancer. It call health check in loop to
     * figure out that the service is started
     */
    private void client(CountDownLatch latch, List<Object> exceptionList) {
        SparkTestUtil testUtil = new SparkTestUtil(4567);
        while (isClientRunning) {
            try {
                SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/healthcheck", null);
                Assert.assertEquals(200, response.status);
                Assert.assertEquals("Hello World!", response.body);
                latch.countDown();
            } catch (Exception | Error e) {
                exceptionList.add(e);
                LOGGER.error("Client error", e);
            }
        }
    }

    /**
     * Simulates the server with a long list of APIs to be initialized
     */
    private void initHandlers() {
        for (int i = 0; i < NUM_HANDLERS; i++) {
            get("/api" + i, (q, a) -> "Hello World!");
        }
    }

    @Test
    public void testInitialization() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);
        List<Object> exceptionList = new ArrayList<>();
        get("/healthcheck", (q, a) -> "Hello World!");
        // Run the client in its own Thread
        Thread clientThread = new Thread(() -> client(latch, exceptionList));
        clientThread.start();
        latch.await();
        initHandlers();
        isClientRunning = false;
        clientThread.join();
        assertEquals(0, exceptionList.size());
    }

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

}
