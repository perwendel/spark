package spark.servlet;

import java.io.File;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

public class MyApp implements SparkApplication {

    public static final String EXTERNAL_FILE = "externalFileServlet.html";

    static File tmpExternalFile;

    @Override
    public synchronized void init() {
        try {
            externalStaticFileLocation(System.getProperty("java.io.tmpdir"));
            staticFileLocation("/public");

            tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), EXTERNAL_FILE);
            FileWriter writer = new FileWriter(tmpExternalFile);
            writer.write("Content of external file");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        before("/protected/*", (request, response) -> {
            halt(401, "Go Away!");
        });

        get("/hi", (request, response) -> {
            return "Hello World!";
        });

        get("/async", (request, response) -> {
            Path path = Paths.get(ClassLoader.getSystemResource("public/page.html").toURI());
            AsynchronousFileChannel fileChannel =
                AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(100);
            CompletableFuture future = new CompletableFuture<>();
            fileChannel.read(buffer, 0, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    future.complete("Hello Async!");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    future.completeExceptionally(exc);
                }
            });
            return future;
        });

        get("/async-exception", (request, response) -> {
            Path path = Paths.get(ClassLoader.getSystemResource("public/page.html").toURI());
            AsynchronousFileChannel fileChannel =
                AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(100);
            CompletableFuture future = new CompletableFuture<>();
            fileChannel.read(buffer, 0, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    future.complete("Hello Async!");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    future.completeExceptionally(exc);
                }
            });
            return future;
        });

        get("/:param", (request, response) -> {
            return "echo: " + request.params(":param");
        });

        get("/", (request, response) -> {
            return "Hello Root!";
        });

        post("/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        after("/hi", (request, response) -> {
            response.header("after", "foobar");
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

}
