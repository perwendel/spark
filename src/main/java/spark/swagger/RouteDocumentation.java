package spark.swagger;

import java.util.List;

/**
 * Created by magrnw on 3/8/17.
 */
public class RouteDocumentation {
    private String summary;
    private String description;
    private String operationId;

    private Payload consumes;
    private Payload produces;
    private Parameters parameters;
    private Responses responses;
    private Tags tags;

    public String getSummary() {
        return summary;
    }

    public RouteDocumentation(String tagName) {
        tags(new Tags().tag(tagName));
    }

    public RouteDocumentation() {}

    public RouteDocumentation summary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RouteDocumentation description(String description) {
        this.description = description;
        return this;
    }

    public String getOperationId() {
        return operationId;
    }

    public RouteDocumentation operationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public Payload getConsumes() {
        return consumes;
    }

    public RouteDocumentation consumes(Payload consumes) {
        this.consumes = consumes;
        return this;
    }

    public Payload getProduces() {
        return produces;
    }

    public RouteDocumentation produces(Payload produces) {
        this.produces = produces;
        return this;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public RouteDocumentation parameters(Parameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public Responses getResponses() {
        return responses;
    }

    public RouteDocumentation responses(Responses responses) {
        this.responses = responses;
        return this;
    }

    public Tags getTags() {
        return tags;
    }

    public RouteDocumentation tags(Tags tags) {
        this.tags = tags;
        return this;
    }
}
