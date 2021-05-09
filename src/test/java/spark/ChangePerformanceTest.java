package spark;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static spark.Spark.*;

// A simple performance test in test suite to gauge changes effect on performance.
public class ChangePerformanceTest {

    public static final String BOOKS = "/books";

    public static final int PORT = 4567;

    private static final SparkTestUtil http = new SparkTestUtil(PORT);

    private static Map<String, Book> books = new HashMap<String, Book>();

    private static final int testNumber = 1000;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {

        final Random random = new Random();

        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent in the post body as x-www-urlencoded values e.g. author=Foo&title=Bar
        // you get them by using request.queryParams("valuename")
        post("/books", (request, response) -> {
            String author = request.queryParams("author");
            String title = request.queryParams("title");
            Book book = new Book(author, title);

            int id = random.nextInt(Integer.MAX_VALUE);
            books.put(String.valueOf(id), book);

            response.status(201); // 201 Created
            return id;
        });

        // Gets the book resource for the provided id
        get("/books/:id", (request, response) -> {
            Book book = books.get(request.params(":id"));
            if (book != null) {
                return "Title: " + book.getTitle() + ", Author: " + book.getAuthor();
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Updates the book resource for the provided id with new information
        // author and title are sent in the request body as x-www-urlencoded values e.g. author=Foo&title=Bar
        // you get them by using request.queryParams("valuename")
        put("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.get(id);
            if (book != null) {
                String newAuthor = request.queryParams("author");
                String newTitle = request.queryParams("title");
                if (newAuthor != null) {
                    book.setAuthor(newAuthor);
                }
                if (newTitle != null) {
                    book.setTitle(newTitle);
                }
                return "Book with id '" + id + "' updated";
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Deletes the book resource for the provided id
        delete("/books/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.remove(id);
            if (book != null) {
                return "Book with id '" + id + "' deleted";
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Gets all available book resources (ids)
        get("/books", (request, response) -> {
            StringBuilder ids = new StringBuilder();
            for (String id : books.keySet()) {
                ids.append(id).append(" ");
            }
            return ids.toString();
        });

        Spark.awaitInitialization();
    }

    @Test
    public void testPostPerformance() {
        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            String author = "Foo";
            String title = "Bar";
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < testNumber; i++) {
                String path = BOOKS + "?author=" + author + i + "&title=" + title + i;
                http.doMethod("POST", path, "", false, "*/*", requestHeader);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("The time to run the POST method " + testNumber + " times is: " + (endTime - startTime) + "ms");
            assertEquals(testNumber, books.size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPutPerformance() {
        try {
            assertEquals(testNumber, books.size());
            String[] booksId = new String[testNumber];
            String[] reStr = new String[testNumber];
            int cnt = 0;
            for (Map.Entry<String, Book> entry : books.entrySet()) {
                booksId[cnt++] = entry.getKey();
            }
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            String author = "Woo";
            String title = "Tar";
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < testNumber; i++) {
                String path = BOOKS + "/" + booksId[i] + "?author=" + author + i + "&title=" + title + i;
                reStr[i] = http.doMethod("PUT", path, "", false, "*/*", requestHeader).body;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("The time to run the PUT method " + testNumber + " times is: " + (endTime - startTime) + "ms");
            for (int i = 0; i < testNumber; i++) {
                assertEquals("Book with id '" + booksId[i] + "' updated", reStr[i]);
            }
            assertEquals(testNumber, books.size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeletePerformance() {
        try {
            assertEquals(testNumber, books.size());
            String[] booksId = new String[testNumber];
            String[] reStr = new String[testNumber];
            int cnt = 0;
            for (Map.Entry<String, Book> entry : books.entrySet()) {
                booksId[cnt++] = entry.getKey();
            }
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < testNumber; i++) {
                reStr[i] = http.doMethod("DELETE", BOOKS + "/" + booksId[i], "", false, "*/*", requestHeader).body;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("The time to run the DELETE method " + testNumber + " times is: " + (endTime - startTime) + "ms");
            for (int i = 0; i < testNumber; i++) {
                assertEquals("Book with id '" + booksId[i] + "' deleted", reStr[i]);
            }
            assertEquals(0, books.size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static class Book {

        public String author, title;

        public Book(String author, String title) {
            this.author = author;
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
