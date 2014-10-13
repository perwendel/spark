package spark;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.route.HttpMethod;
import spark.util.SparkTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RoutePerformanceTest extends AbstractBenchmark {
    static SparkTestUtil testUtil;
    private static final String[] routePaths = {
        "/",
        "/projects",
        "/projects/:projectId",
        "/projects/:projectId/todos",
        "/projects/:projectId/todos/:todoId",
        "/projects/:projectId/files",
        "/projects/:projectId/files/:fileid",
        "/projects/:projectId/messages",
        "/projects/:projectId/messages/:messageId",
        "/projects/:projectId/messages/:messageId/replies",
        "/projects/:projectId/messages/:messageId/replies/:replyId",
        "/projects/:projectId/permissions",
        "/projects/:projectId/permissions/:permissionId",
        "/users",
        "/users/:userId",
        "/users/:userId/messages",
        "/users/:userId/messages/:messageId",
        "/repos",
        "/repos/:repoId",
        "/repos/:repoId/pull_requests",
        "/repos/:repoId/pull_requests/:pullRequestId",
        "/repos/:repoId/pull_requests/:pullRequestId/comments",
        "/repos/:repoId/pull_requests/:pullRequestId/comments/:commentId",
        "/repos/:repoId/pull_requests/:pullRequestId/comments/:commentId/replies",
        "/repos/:repoId/pull_requests/:pullRequestId/comments/:commentId/replies/:replyId"
    };
    private static final int COUNT = 1000;
    private static final List<TestRequest> requests = new ArrayList<>();
    private static final HttpMethod[] methods = {HttpMethod.get, HttpMethod.post, HttpMethod.put};
    private static final String[] accepts = {"text/html", "application/json", "x-whatever/something"};


    private static class TestRequest {
        public HttpMethod method;
        public String path;
        public String accept;
    }

    @AfterClass
    public static void teardown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() {
        testUtil = new SparkTestUtil(4567);

        for (String path : routePaths) {
            for (HttpMethod method : methods) {
                for (String accept : accepts) {
                    Spark.addRoute(method.name(), Spark.wrap(path, accept, new Route() {
                        @Override
                        public Object handle(Request request, Response response) {
                            return "Hello, world";
                        }
                    }));

                }
            }
        }

        final Random random = new Random();
        for (int i = 0; i < COUNT; i++) {
            TestRequest req = new TestRequest();
            req.path = routePaths[random.nextInt(routePaths.length)].replaceAll("\\/\\:\\w+", "/123");
            req.method = methods[random.nextInt(methods.length)];
            req.accept = accepts[random.nextInt(accepts.length)];
            requests.add(i, req);
        }

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }
    @Test
    public void doBenchmark() {
        try {
            for (TestRequest req : requests) {
                testUtil.doMethod(req.method.name().toUpperCase(), req.path, "", req.accept);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
