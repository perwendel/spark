package spark.session;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class SessionFactory {
    public static ISessionStrategy getSession(SessionType type) {
        switch (type) {
            case Jetty:
                return new JettySessionStrategy();
            case Cookie:
                return new CookieSessionStrategy();
            default:
                throw new IllegalArgumentException("Unknown session type.");
        }
    }
}
