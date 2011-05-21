package spark;

import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import spark.examples.books.Books;
import spark.utils.IOUtils;

public class SparkIntegrationTest {

   private static int PORT = 9191;
   
   @BeforeClass
   public static void setup() {
      Spark.setPort(PORT);
      Books.main(null);
      try {
         Thread.sleep(500);
      } catch (Exception e) {}
   }

   private static String id;
   
   @Test
   public void testCreateBook() {
      try {
         UrlResponse response = doMethod("POST", "http://localhost:" +PORT + "/books?author=FOO&title=BAR", null);
         id = response.body;
         Assert.assertNotNull(response);
         Assert.assertNotNull(response.body);
         Assert.assertTrue(Integer.valueOf(response.body) > 0);
         Assert.assertEquals(201, response.status);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
   
   @Test
   public void testListBooks() {
      try {
         UrlResponse response = doMethod("GET", "http://localhost:" + PORT + "/books", null);
         Assert.assertNotNull(response);
         String body = response.body.trim();
         Assert.assertNotNull(body);
         Assert.assertTrue(Integer.valueOf(body) > 0);
         Assert.assertEquals(200, response.status);
         Assert.assertTrue(response.body.contains(id));
      } catch (Throwable e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }
   
   private static UrlResponse doMethod(String requestMethod, String urlStr, String body) throws Exception {
      URL url = new URL(urlStr);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(requestMethod);
      
      // connection.setDoOutput(true);
      
      connection.connect();
      // connection.getOutputStream().flush();
      
      String res = IOUtils.toString(connection.getInputStream());
      UrlResponse response = new UrlResponse();
      response.body = res;
      response.status = connection.getResponseCode();
      return response;
   }
   
   private static class UrlResponse {
      private String body;
      private int status;
   }

}
