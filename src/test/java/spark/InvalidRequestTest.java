package spark;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import static spark.Spark.halt;

public class InvalidRequestTest {
    @Test
    public void invalidRequestTest(){
        Service service = Service.ignite().port(4567);
        service.staticFiles.externalLocation("/Users/");

        service.get("/", (req, res) -> {
            if (!req.requestMethod().equalsIgnoreCase("GET")) {
                halt(401, "invalid Http method");
            }
            return null;
        });

        String result = "";
        String url = "http://localhost:4567";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Method", "XYZ");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            return;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        assertEquals("", result);
    }
}
