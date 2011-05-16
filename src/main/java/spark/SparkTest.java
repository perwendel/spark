package spark;

public class SparkTest {
    
    public static void main(String[] args) {
        Spark.setPort(4567);
        
        Spark.get("/sport/:section", new Function() {
            @Override
            public Object exec() {
                return "these are the sport pages, section: " + params(":section") + ", " + request.ip();
            }
        });
        
        Spark.get("/news/:section", new Function() {
            @Override
            public Object exec() {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + params("section") + "</news>";
            }
        });
        
        Spark.get("/redirect", new Function() {
            @Override
            public Object exec() {
                redirect("/news/world");
                return null;
            }
        });
        
        Spark.get("/", new Function() {
            @Override
            public Object exec() {
                set(null, null, null);
                return "root";
            }
        });
        
        Spark.post("/", new Function() {
            @Override
            public Object exec() {
                return "echo: " + request.body();
            }
        });
    }
}
