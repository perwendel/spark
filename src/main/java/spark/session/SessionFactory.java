package spark.session;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class SessionFactory {
    public static ISessionStrategy getSession(SessionType type, KeyPair encryptionKeyPair, KeyPair signingKeyPair)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        switch (type) {
            case Jetty:
                return new JettySessionStrategy();
            case Cookie:
                return new CookieSessionStrategy(encryptionKeyPair, signingKeyPair);
            default:
                throw new IllegalArgumentException("Unknown session type.");
        }
    }
}
