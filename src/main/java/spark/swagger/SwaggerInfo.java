package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class SwaggerInfo {

    private String description;
    private String version;
    private String title;
    private String termsOfService;
    private Contact contact;
    private License license;

    public String getDescription() {
        return description;
    }

    public SwaggerInfo description(String description) {
        this.description = description;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public SwaggerInfo version(String version) {
        this.version = version;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SwaggerInfo title(String title) {
        this.title = title;
        return this;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public SwaggerInfo termsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
        return this;
    }

    public Contact getContact() {
        return contact;
    }

    public SwaggerInfo contact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public License getLicense() {
        return license;
    }

    public SwaggerInfo license(License license) {
        this.license = license;
        return this;
    }
}
