package spark;
import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

import static spark.Spark.*;

public class MyTest {
    public static void main(String[] arg){
        get("/hello", (req, res) -> req.host());
    }
}
