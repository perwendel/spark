package spark.examples.exception;

public class JWGmeligMeylingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public String trustButVerify() {
        return "The fact that it doesn't break with more explicit types should be enough, but tipsy is a worrywart";
    }
}
