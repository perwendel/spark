package spark;

import com.google.gson.Gson;

//CS304 Issue link: https://github.com/perwendel/spark/issues/534
public class TransformerExtend implements ResponseTransformerExtend {

    @Override
    public String render(Object model, Request req, Response res) throws Exception {
        if(req.contentType().equals("application/json")) {
            Gson gson = new Gson();
            res.type("application/json");
            System.out.println(res.type());
            return gson.toJson(model);
        }
        return "Not Json type message. Other types are not implemented.";
    }
}
