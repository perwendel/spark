//package spark;
//
//import java.io.FileNotFoundException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//import java.util.Map;
//
//import junit.framework.Assert;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import spark.examples.books.Books;
//import spark.utils.IOUtils;
//
//public class BooksIntegrationTest {
//
//   private static int PORT = 4567;
//
//   private static String AUTHOR = "FOO";
//   private static String TITLE = "BAR";
//   private static String NEW_TITLE = "SPARK";
//
//   @AfterClass
//   public static void tearDown() {
//       Spark.clearRoutes();
//       Spark.stop();
//   }
//   
//   @BeforeClass
//   public static void setup() {
//      Spark.before(new Filter(){
//         @Override
//         public void handle(Request request, Response response) {
//            response.header("FOZ", "BAZ");
//         }
//      });
//      
//      Books.main(null);
//      
//      Spark.after(new Filter(){
//         @Override
//         public void handle(Request request, Response response) {
//            response.header("FOO", "BAR");
//         }
//      });
//      try {
//         Thread.sleep(500);
//      } catch (Exception e) {
//      }
//   }
//
//   private static String id;
//
//   @Test
//   public void testCreateBook() {
//      try {
//         UrlResponse response = doMethod("POST", "/books?author=" + AUTHOR + "&title=" + TITLE, null);
//         id = response.body.trim();
//         Assert.assertNotNull(response);
//         Assert.assertNotNull(response.body);
//         Assert.assertTrue(Integer.valueOf(response.body) > 0);
//         Assert.assertEquals(201, response.status);
//      } catch (Throwable e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testListBooks() {
//      try {
//         UrlResponse response = doMethod("GET", "/books", null);
//         Assert.assertNotNull(response);
//         String body = response.body.trim();
//         System.out.println("BODY: " + body);
//         Assert.assertNotNull(body);
//         Assert.assertTrue(Integer.valueOf(body) > 0);
//         Assert.assertEquals(200, response.status);
//         Assert.assertTrue(response.body.contains(id));
//      } catch (Throwable e) {
//         e.printStackTrace();
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testGetBook() {
//      try {
//		 // ensure there is a book
//		 testCreateBook();
//		 
//         UrlResponse response = doMethod("GET", "/books/" + id, null);
//         String result = response.body;
//         Assert.assertNotNull(response);
//         Assert.assertNotNull(response.body);
//         Assert.assertEquals(200, response.status);
//         Assert.assertTrue(result.contains(AUTHOR));
//         Assert.assertTrue(result.contains(TITLE));
//       
//         // verify response header set by filters:
//         Assert.assertTrue(response.headers.get("FOZ").get(0).equals("BAZ"));
//         Assert.assertTrue(response.headers.get("FOO").get(0).equals("BAR"));
//		 
//		 // delete the book again
//		 //Comment this delete to ensure the running of the tests
//		 //testDeleteBook();
//      } catch (Throwable e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testUpdateBook() {
//      try {
//         UrlResponse response = doMethod("PUT", "/books/" + id + "?title=" + NEW_TITLE, null);
//         String result = response.body;
//         Assert.assertNotNull(response);
//         Assert.assertNotNull(response.body);
//         Assert.assertEquals(200, response.status);
//         Assert.assertTrue(result.contains(id));
//         Assert.assertTrue(result.contains("updated"));
//      } catch (Throwable e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testGetUpdatedBook() {
//      try {
//         UrlResponse response = doMethod("GET", "/books/" + id, null);
//         String result = response.body;
//         Assert.assertNotNull(response);
//         Assert.assertNotNull(response.body);
//         Assert.assertEquals(200, response.status);
//         Assert.assertTrue(result.contains(AUTHOR));
//         Assert.assertTrue(result.contains(NEW_TITLE));
//      } catch (Throwable e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testDeleteBook() {
//      try {
//         UrlResponse response = doMethod("DELETE", "/books/" + id, null);
//         String result = response.body;
//         Assert.assertNotNull(response);
//         Assert.assertNotNull(response.body);
//         Assert.assertEquals(200, response.status);
//         Assert.assertTrue(result.contains(id));
//         Assert.assertTrue(result.contains("deleted"));
//      } catch (Throwable e) {
//         throw new RuntimeException(e);
//      }
//   }
//
//   @Test
//   public void testBookNotFound() {
//      try {
//         doMethod("GET", "/books/" + id, null);
//      } catch (Exception e) {
//         if (e instanceof FileNotFoundException) {
//            Assert.assertTrue(true);
//         } else {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//         }
//      }
//   }
//
//   private static UrlResponse doMethod(String requestMethod, String path, String body) throws Exception {
//      URL url = new URL("http://localhost:" + PORT + path);
//      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//      connection.setRequestMethod(requestMethod);
//
//      // connection.setDoOutput(true);
//
//      connection.connect();
//      // connection.getOutputStream().flush();
//
//      String res = IOUtils.toString(connection.getInputStream());
//      UrlResponse response = new UrlResponse();
//      response.body = res;
//      response.status = connection.getResponseCode();
//      response.headers = connection.getHeaderFields();
//      return response;
//   }
//
//   private static class UrlResponse {
//      public Map<String, List<String>> headers;
//      private String body;
//      private int status;
//   }
//
//}
