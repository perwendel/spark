package spark;

import static spark.Spark.*;


// Try to fix issue 1026: https://github.com/perwendel/spark/issues/1077
// I am not sure whether it is a bug, because it is tagged as Bug ..?
// But I think it conflict with the documentation, so I try to fix it
// In short, we expect the input and output are:
// curl -i -H "Accept: application/json" http://localhost:4567/hello : Hello application json
// curl -i -H "Accept: text/html" http://localhost:4567/hello : Go Away!!!
// curl http://localhost:4567/hello : Hello application json
// The first and second are right, but now the last command will get output: Go Away!!!
// I think it is not reasonable because the empty acceptType should match every possibilities
// so with the earliest match, it should match the first possible acceptType
// Therefore, you can exchange the 20th and 22th line to check whether it match the first type

public class Issue1077Test {
    public static void main(String[] args) {
        get("/hello","application/json", (request, response) -> "{\"message\": \"Hello application json\"}");

        get("/hello","text/json", (request, response) -> "{\"message\": \"Hello text json\"}");

        get("/hello", (request, response) -> {
            response.status(406);
            return "Go Away!!!";
        });
    }
}
