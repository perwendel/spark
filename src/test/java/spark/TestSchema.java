package spark;

import spark.swagger.Contact;
import spark.swagger.License;
import spark.swagger.Parameter;
import spark.swagger.Parameters;
import spark.swagger.RootTags;
import spark.swagger.RouteDocumentation;
import spark.swagger.Schema;
import spark.swagger.SwaggerInfo;

import static spark.Spark.post;

/**
 * Created by magrnw on 4/21/17.
 */
public class TestSchema {
    public class WeatherSettings {

        private transient Long id = 1L;

        private String apiKey;

        private String country;

        private String state;

        private String city;
    }

    public static void main(String args[]) {
        Spark.swagger("/swagger.json")
            .host("127.0.0.1:8080")
            .tags(new RootTags().tag("foo", "Everything about foo"))
            .info(new SwaggerInfo()
                .contact(new Contact().email("matt@mjgreenwood.net"))
                .description("Verification of swagger documentation")
                .license(new License().name("Apache v2.0")
                    .url("http://www.foo.com"))
            )
            .basePath("/")
            .version("1.1.0")
            .swaggerVersion("2.0");

        post("/v1/openweather/settings",
            new RouteDocumentation("foo")
                .description("Define the OpenWeather settings such as the apiKey")
                .operationId("postSettings")
                .parameters(new Parameters()
                    .parameter(new Parameter("settings")
                        .type(Parameter.Type.string)
                        .in(Parameter.In.body)
                        .schema(new Schema(WeatherSettings.class))
                        .description("The API Settings definition"))
                )
                .summary("Define the OpenWeather settings"),
            (request, response) -> "");

        //SwaggerDoc doc = Spark.swagger("/foo.json");

    }

}
