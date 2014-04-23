package spark;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spark.SparkJ8.after;
import static spark.SparkJ8.before;
import static spark.SparkJ8.stop;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.examples.Books;
import spark.examples.BooksJ8;
import spark.utils.IOUtils;

public class BooksIntegrationTest {

    private static class UrlResponse {
        public Map<String, List<String>> headers;
        private String body;
        private int status;
    }

    private static final int PORT = 4567;
    private static final String AUTHOR = "FOO", TITLE = "BAR", NEW_TITLE = "SPARK";

    private static String id = "1";

    private static UrlResponse doMethod (String requestMethod, String path)
        throws FileNotFoundException {

        try {
            URL url = new URL ("http://localhost:" + PORT + path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection ();
            connection.setRequestMethod (requestMethod);

            connection.connect ();

            String res = IOUtils.toString (connection.getInputStream ());
            UrlResponse response = new UrlResponse ();
            response.body = res;
            response.status = connection.getResponseCode ();
            response.headers = connection.getHeaderFields ();
            return response;
        }
        catch (FileNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    @AfterClass public static void tearDown () throws InterruptedException {
        stop ();
        sleep (500);
    }

    private static void setupJ7 () {
        before (
            new Filter () {
                @Override public void handle (Request req, Response res) {
                    res.header ("FOZ", "BAZ");
                }
            }
        );
        Books.books ();
        after (new Filter () {
            @Override public void handle (Request req, Response res) {
                res.header ("FOO", "BAR");
            }
        });
    }

    private static void setupJ8 () {
        before (it -> it.header ("FOZ", "BAZ"));
        BooksJ8.books ();
        after (it -> it.header ("FOO", "BAR"));
    }

    @BeforeClass public static void setup () throws InterruptedException {
        setupJ7();
        setupJ8 ();
        sleep (500);
    }

    private void createBook (String pref) throws FileNotFoundException {
        UrlResponse res =
            doMethod ("POST", pref + "/books?author=" + AUTHOR + "&title=" + TITLE);
        id = res.body.trim ();

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (Integer.valueOf (res.body) > 0);
        assertEquals (201, res.status);
    }

    private void listBooks (String pref) throws FileNotFoundException {
        createBook (pref);
        UrlResponse res = doMethod ("GET", pref + "/books");

        assertNotNull (res);
        assertNotNull (res.body.trim ());
        assertTrue (res.body.trim ().length () > 0);
        assertTrue (res.body.contains (id));
        assertEquals (200, res.status);

        System.out.println ("BODY: " + res.body.trim ());
    }

    private void getBook (String pref) throws FileNotFoundException {
        // ensure there is a book
        createBook (pref);
        UrlResponse res = doMethod ("GET", pref + "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (AUTHOR));
        assertTrue (res.body.contains (TITLE));
        assertEquals (200, res.status);

        // verify response header set by filters:
        assertTrue (res.headers.get ("FOZ").get (0).equals ("BAZ"));
        assertTrue (res.headers.get ("FOO").get (0).equals ("BAR"));
    }

    private void updateBook (String pref) throws FileNotFoundException {
        createBook (pref);
        UrlResponse res = doMethod ("PUT", pref + "/books/" + id + "?title=" + NEW_TITLE);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (id));
        assertTrue (res.body.contains ("updated"));
        assertEquals (200, res.status);
    }

    private void getUpdatedBook (String pref) throws FileNotFoundException {
        updateBook (pref);
        UrlResponse res = doMethod ("GET", pref + "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (AUTHOR));
        assertTrue (res.body.contains (NEW_TITLE));
        assertEquals (200, res.status);
    }

    private void deleteBook (String pref) throws FileNotFoundException {
        UrlResponse res = doMethod ("DELETE", pref + "/books/" + id);

        assertNotNull (res);
        assertNotNull (res.body);
        assertTrue (res.body.contains (id));
        assertTrue (res.body.contains ("deleted"));
        assertEquals (200, res.status);
    }

    @Test public void createBook () throws FileNotFoundException { createBook (""); }

    @Test public void listBooks () throws FileNotFoundException { listBooks (""); }

    @Test public void getBook () throws FileNotFoundException { getBook (""); }

    @Test public void updateBook () throws FileNotFoundException { updateBook (""); }

    @Test public void getUpdatedBook () throws FileNotFoundException { getUpdatedBook (""); }

    @Test public void deleteBook () throws FileNotFoundException { deleteBook (""); }

    @Test (expected = FileNotFoundException.class)
    public void bookNotFound () throws FileNotFoundException {
        doMethod ("GET", "/books/" + 9999);
    }

    @Test public void createBookJ8 () throws FileNotFoundException { createBook ("/j8"); }

    @Test public void listBooksJ8 () throws FileNotFoundException { listBooks ("/j8"); }

    @Test public void getBookJ8 () throws FileNotFoundException { getBook ("/j8"); }

    @Test public void updateBookJ8 () throws FileNotFoundException { updateBook ("/j8"); }

    @Test public void getUpdatedBookJ8 () throws FileNotFoundException {
        getUpdatedBook ("/j8");
    }

    @Test public void deleteBookJ8 () throws FileNotFoundException { deleteBook ("/j8"); }

    @Test (expected = FileNotFoundException.class)
    public void bookNotFoundJ8 () throws FileNotFoundException {
        doMethod ("GET", "/j8/books/" + 9999);
    }
}
