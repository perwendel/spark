package spark.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class CookieSessionStrategy implements ISessionStrategy {
    private final CookieSessionHandler sessionHandler;
    private static final Logger logger = LoggerFactory.getLogger(CookieSessionStrategy.class);

    public CookieSessionStrategy(String encryptionKey) throws NoSuchAlgorithmException {
        sessionHandler = new CookieSessionHandler(encryptionKey);
    }

    @Override
    public HttpSession getSession(HttpServletRequest request, boolean create) {
        try {
            return sessionHandler.readSession(request);
        } catch (Exception e) {
            // could not read session
            return new CookieSession(request);
        }
    }

    @Override
    public void writeSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            sessionHandler.writeSession(request, response);
        } catch (Exception e) {
            logger.error("Error writing session.");
            Spark.halt(500);
        }
    }
}
