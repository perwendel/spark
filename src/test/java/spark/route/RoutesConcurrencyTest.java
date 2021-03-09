package spark.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spark.FilterImpl;
import spark.RouteImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoutesConcurrencyTest {
    private final AtomicInteger numberOfSuccessfulIterations = new AtomicInteger();
    private final AtomicInteger numberOfFailedIterations = new AtomicInteger();
    private ExecutorService executorService;

    private static final int NUMBER_OF_ITERATIONS = 10_000;
    private static final int NUMBER_OF_THREADS = 2;
    private static final int NUMBER_OF_TASKS = NUMBER_OF_THREADS;

    private static final String ROUTE_PATH_PREFIX = "/route/";
    private static final String FILTER_PATH_PREFIX = "/filter/";

    @Before
    public void setup() {
        numberOfSuccessfulIterations.set(0);
        numberOfFailedIterations.set(0);
    }

    @After
    public void teardown() {
        if (executorService != null && !executorService.isShutdown()) {
            try {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void classShouldBeThreadSafe() throws Exception {
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Collection<Callable<Void>> tasks = partitionIterationsIntoTasks();
        List<Future<Void>> futureResults = executorService.invokeAll(tasks);
        executorService.shutdown();
        for (Future<Void> futureResult : futureResults) {
            futureResult.get();
            assertTrue(futureResult.isDone());
        }
        assertEquals(NUMBER_OF_ITERATIONS, numberOfSuccessfulIterations.intValue());
        assertEquals(0, numberOfFailedIterations.intValue());
    }

    private Collection<Callable<Void>> partitionIterationsIntoTasks() {
        final Collection<Callable<Void>> tasks = new ArrayList<>();
        final Routes routes = Routes.create();
        final int numberOfIterationsPerTask = NUMBER_OF_ITERATIONS / NUMBER_OF_TASKS;
        for (int taskIndex = 0; taskIndex < NUMBER_OF_TASKS; taskIndex++) {
            final int fromIteration = numberOfIterationsPerTask * taskIndex;
            final int toIteration = numberOfIterationsPerTask * (taskIndex + 1);
            tasks.add(() -> {
                for (int iterationIndex = fromIteration; iterationIndex < toIteration; iterationIndex++) {
                    try {
                        String routePath = ROUTE_PATH_PREFIX + iterationIndex;
                        String filterPath = FILTER_PATH_PREFIX + iterationIndex;
                        routes.add(HttpMethod.get, RouteImpl.create(routePath, RouteImpl.DEFAULT_ACCEPT_TYPE, null));
                        routes.add(HttpMethod.get, FilterImpl.create(filterPath, FilterImpl.DEFAULT_ACCEPT_TYPE, null));
                        routes.find(HttpMethod.get, routePath, RouteImpl.DEFAULT_ACCEPT_TYPE);
                        routes.findMultiple(HttpMethod.get, filterPath, FilterImpl.DEFAULT_ACCEPT_TYPE);
                        routes.findAll();
                        routes.remove(routePath, HttpMethod.get.toString());
                        routes.remove(filterPath);
                        routes.clear();
                        numberOfSuccessfulIterations.getAndIncrement();
                    } catch (Exception e) {
                        numberOfFailedIterations.getAndIncrement();
                    }
                }
                return null;
            });
        }
        return tasks;
    }
}
