package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import spark.examples.books.Books;
import spark.utils.IOUtils;

public class BooksIntegrationTest {

    private static int PORT = 4567;

    private static String AUTHOR = "FOO";
    private static String TITLE = "BAR";
    private static String NEW_TITLE = "SPARK";

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() {
        Spark.before(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.header("FOZ", "BAZ");
            }
        });

        Books.main(null);

        Spark.after(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.header("FOO", "BAR");
            }
        });
        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    private static String bookId;

    @Test
    public void testCreateBook() {
        try {
            UrlResponse response = createBookViaPOST();

            assertNotNull(response);
            assertNotNull(response.body);
            assertTrue(Integer.valueOf(response.body) > 0);
            assertEquals(201, response.status);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testListBooks() {
        try {
            bookId = createBookViaPOST().body.trim();

            UrlResponse response = doMethod("GET", "/books", null);
            assertNotNull(response);
            String body = response.body.trim();
            System.out.println("BODY: " + body);
            assertNotNull(body);
            assertTrue(Integer.valueOf(body) > 0);
            assertEquals(200, response.status);
            assertTrue(response.body.contains(bookId));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetBook() {
        try {
            bookId = createBookViaPOST().body.trim();

            UrlResponse response = doMethod("GET", "/books/" + bookId, null);
            String result = response.body;
            assertNotNull(response);
            assertNotNull(response.body);
            assertEquals(200, response.status);
            assertTrue(result.contains(AUTHOR));
            assertTrue(result.contains(TITLE));

            // verify response header set by filters:
            assertTrue(response.headers.get("FOZ").get(0).equals("BAZ"));
            assertTrue(response.headers.get("FOO").get(0).equals("BAR"));

            // delete the book again
            // Comment this delete to ensure the running of the tests
            // testDeleteBook();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Ignore
    @Test
    public void testUpdateBook() {
        try {
            UrlResponse response = doMethod("PUT", "/books/" + bookId + "?title=" + NEW_TITLE, null);
            String result = response.body;
            assertNotNull(response);
            assertNotNull(response.body);
            assertEquals(200, response.status);
            assertTrue(result.contains(bookId));
            assertTrue(result.contains("updated"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Ignore
    @Test
    public void testGetUpdatedBook() {
        try {
            UrlResponse response = doMethod("GET", "/books/" + bookId, null);
            String result = response.body;
            assertNotNull(response);
            assertNotNull(response.body);
            assertEquals(200, response.status);
            assertTrue(result.contains(AUTHOR));
            assertTrue(result.contains(NEW_TITLE));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteBook() {
        try {
            UrlResponse response = doMethod("DELETE", "/books/" + bookId, null);
            String result = response.body;
            assertNotNull(response);
            assertNotNull(response.body);
            assertEquals(200, response.status);
            assertTrue(result.contains(bookId));
            assertTrue(result.contains("deleted"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBookNotFound() {
        try {
            doMethod("GET", "/books/" + bookId, null);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                assertTrue(true);
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static UrlResponse doMethod(String requestMethod, String path, String body) throws Exception {
        URL url = new URL("http://localhost:" + PORT + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        // connection.setDoOutput(true);

        connection.connect();
        // connection.getOutputStream().flush();

        String res = IOUtils.toString(connection.getInputStream());
        UrlResponse response = new UrlResponse();
        response.body = res;
        response.status = connection.getResponseCode();
        response.headers = connection.getHeaderFields();
        return response;
    }

    private static class UrlResponse {
        public Map<String, List<String>> headers;
        private String body;
        private int status;
    }

    private UrlResponse createBookViaPOST() throws Exception {
        return doMethod("POST", "/books?author=" + AUTHOR + "&title=" + TITLE, null);
    }
}
