package spark.application;

import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;

import static spark.Spark.get;
import static spark.Spark.setPort;

public abstract class LoadBalancedMicroService
{
    public LoadBalancedMicroService(final int port)
    {
        setPort(port);

        get(new Route("/ping")
        {
            @Override public Object handle(final Request request, final Response response)
            {
                response.status(HttpURLConnection.HTTP_OK);
                return HttpURLConnection.HTTP_OK;
            }
        });
    }
}
