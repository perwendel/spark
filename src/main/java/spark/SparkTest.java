package spark;

public class SparkTest {
    
    public static void main(String[] args) {
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
        
        Spark.get("/", new Function() {
            @Override
            public Object exec() {
                return "root:";
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
