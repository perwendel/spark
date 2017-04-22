package spark.swagger;

import spark.route.RouteEntry;
import spark.route.Routes;

/**
 * Created by magrnw on 3/8/17.
 */
public class SwaggerDoc {
    private String swagger = "2.0";
    private SwaggerInfo info;
    private String host;
    private String basePath;
    private RoutePaths paths = new RoutePaths();
    private String version = "1.0.0";
    private String swaggerPath = "/swagger.json";
    private RootTags tags;
    private Definitions definitions;

    public RoutePaths getPaths() {
        return paths;
    }

    public SwaggerDoc paths(RoutePaths paths) {
        this.paths = paths;
        return this;
    }

    public String getSwagger() {
        return swagger;
    }

    public void calculateDefinitions(Routes routes) {
        definitions = new Definitions();
        for (RouteEntry route : routes.getRoutes()) {
            if (null != route.getDocumentation() && null != route.getDocumentation().getParameters()) {
                for (Parameter parameter : route.getDocumentation().getParameters()) {
                    definitions.put(parameter.getSchema().getClassName(), parameter.getSchema().getDefinition());
                }
            }
        }
    }

    public SwaggerDoc swaggerVersion(String swagger) {
        this.swagger = swagger;
        return this;
    }

    public SwaggerInfo getInfo() {
        return info;
    }

    public SwaggerDoc info(SwaggerInfo info) {
        this.info = info;
        return this;
    }

    public String getHost() {
        return host;
    }

    public SwaggerDoc host(String host) {
        this.host = host;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public SwaggerDoc basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public SwaggerDoc version(String version) {
        this.version = version;
        return this;
    }

    public String swaggerPath() {
        return swaggerPath;
    }

    public void setSwaggerPath(String swaggerPath) {
        this.swaggerPath = swaggerPath;
    }

    public RootTags getTags() {
        return tags;
    }

    public SwaggerDoc tags(RootTags tags) {
        this.tags = tags;
        return this;
    }
}
