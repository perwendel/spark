package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spark.examples.books.Books;
import spark.utils.IOUtils;

public class BooksIntegrationTest {

    private static int PORT = 4567;

    private static String AUTHOR = "FOO";
    private static String TITLE = "BAR";
    private static String NEW_TITLE = "SPARK";

    private String bookId;

    @After
    public void tearDown() {
        Books.books.clear();
        Spark.stop();
    }

    @Before
    public void setup() {
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
            assertTrue(beforeFilterIsSet(response));
            assertTrue(afterFilterIsSet(response));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateBook() {
        try {
            bookId = createBookViaPOST().body.trim();

            UrlResponse response = updateBook();

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

    @Test
    public void testGetUpdatedBook() {
        try {
            bookId = createBookViaPOST().body.trim();
            updateBook();

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
            bookId = createBookViaPOST().body.trim();

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

        connection.connect();

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

    private UrlResponse updateBook() throws Exception {
        return doMethod("PUT", "/books/" + bookId + "?title=" + NEW_TITLE, null);
    }

    private boolean afterFilterIsSet(UrlResponse response) {
        return response.headers.get("FOO").get(0).equals("BAR");
    }

    private boolean beforeFilterIsSet(UrlResponse response) {
        return response.headers.get("FOZ").get(0).equals("BAZ");
    }
}
