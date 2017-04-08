package spark;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import static spark.Service.ignite;

public class InitExceptionHandlerTest {

    @Test
    public void testInitExceptionHandler() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executorService.submit(() -> {
            Service service1 = ignite().port(1122);
            service1.init();
            nap();
            Service service2 = ignite().port(1122);
            service2.initExceptionHandler((e) -> future.complete("Custom init error"));
            service2.init();
            service1.stop();
            service2.stop();
        });
        Assert.assertEquals("Custom init error", future.get());
    }

    private void nap() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

}
