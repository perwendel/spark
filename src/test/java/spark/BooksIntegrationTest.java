package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spark.Spark.after;
import static spark.Spark.before;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.examples.books.Books;
import spark.utils.IOUtils;

public class BooksIntegrationTest {

    private static int PORT = 4567;

    private static String AUTHOR = "FOO";
    private static String TITLE = "BAR";
    private static String NEW_TITLE = "SPARK";

    private String bookId;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @After
    public void clearBooks() {
        Books.books.clear();
    }

    @BeforeClass
    public static void setup() {
        before((request, response) -> {
            response.header("FOZ", "BAZ");
        });

        Books.main(null);

        after((request, response) -> {
            response.header("FOO", "BAR");
        });

        Spark.awaitInitialization();
    }

    @Test
    public void canCreateBook() {
        UrlResponse response = createBookViaPOST();

        assertNotNull(response);
        assertNotNull(response.body);
        assertTrue(Integer.valueOf(response.body) > 0);
        assertEquals(201, response.status);
    }

    @Test
    public void canListBooks() {
        bookId = createBookViaPOST().body.trim();

        UrlResponse response = doMethod("GET", "/books", null);

        assertNotNull(response);
        String body = response.body.trim();
        assertNotNull(body);
        assertTrue(Integer.valueOf(body) > 0);
        assertEquals(200, response.status);
        assertTrue(response.body.contains(bookId));
    }

    @Test
    public void canGetBook() {
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
    }

    @Test
    public void canUpdateBook() {
        bookId = createBookViaPOST().body.trim();

        UrlResponse response = updateBook();

        String result = response.body;
        assertNotNull(response);
        assertNotNull(response.body);
        assertEquals(200, response.status);
        assertTrue(result.contains(bookId));
        assertTrue(result.contains("updated"));
    }

    @Test
    public void canGetUpdatedBook() {
        bookId = createBookViaPOST().body.trim();
        updateBook();

        UrlResponse response = doMethod("GET", "/books/" + bookId, null);

        String result = response.body;
        assertNotNull(response);
        assertNotNull(response.body);
        assertEquals(200, response.status);
        assertTrue(result.contains(AUTHOR));
        assertTrue(result.contains(NEW_TITLE));
    }

    @Test
    public void canDeleteBook() {
        bookId = createBookViaPOST().body.trim();

        UrlResponse response = doMethod("DELETE", "/books/" + bookId, null);

        String result = response.body;
        assertNotNull(response);
        assertNotNull(response.body);
        assertEquals(200, response.status);
        assertTrue(result.contains(bookId));
        assertTrue(result.contains("deleted"));
    }

    @Test(expected = FileNotFoundException.class)
    public void wontFindBook() throws IOException {
        getResponse("GET", "/books/" + bookId, null);
    }

    private static UrlResponse doMethod(String requestMethod, String path, String body) {
        UrlResponse response = new UrlResponse();

        try {
            getResponse(requestMethod, path, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static void getResponse(String requestMethod, String path, UrlResponse response)
            throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL("http://localhost:" + PORT + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.connect();
        String res = IOUtils.toString(connection.getInputStream());
        response.body = res;
        response.status = connection.getResponseCode();
        response.headers = connection.getHeaderFields();
    }

    private static class UrlResponse {
        public Map<String, List<String>> headers;
        private String body;
        private int status;
    }

    private UrlResponse createBookViaPOST() {
        return doMethod("POST", "/books?author=" + AUTHOR + "&title=" + TITLE, null);
    }

    private UrlResponse updateBook() {
        return doMethod("PUT", "/books/" + bookId + "?title=" + NEW_TITLE, null);
    }

    private boolean afterFilterIsSet(UrlResponse response) {
        return response.headers.get("FOO").get(0).equals("BAR");
    }

    private boolean beforeFilterIsSet(UrlResponse response) {
        return response.headers.get("FOZ").get(0).equals("BAZ");
    }
}
