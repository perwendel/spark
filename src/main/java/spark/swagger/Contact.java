package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class Contact {
    private String email;

    public Contact email(String email) {
        this.email = email;
        return this;
    }

    public String getEmail() {
        return email;
    }
}
