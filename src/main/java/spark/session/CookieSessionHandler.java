package spark.session;

import org.nustaq.serialization.FSTConfiguration;
import spark.SparkBase;
import spark.webserver.SparkHttpRequestWrapper;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class CookieSessionHandler {
    private static final String session = "session";
    private static final String symmetricEncryptionAlgorithm = "AES";
    private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    private final SecretKey symmetricEncryptionKey;
    private final SecretKey hmacKey;

    static {
        conf.setPreferSpeed(true);
    }

    public CookieSessionHandler(String encryptionKey) throws NoSuchAlgorithmException {

        byte[] key = hash(encryptionKey.getBytes());
        key = Arrays.copyOf(key, 16);
        this.symmetricEncryptionKey = new SecretKeySpec(key, symmetricEncryptionAlgorithm);
        this.hmacKey = new SecretKeySpec(key, "HmacSHA1");
    }

    public CookieSession readSession(HttpServletRequest request) throws InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException {
        Cookie sessionCookie = getCookie(request, CookieSessionHandler.session);
        if (sessionCookie == null) {
            return new CookieSession(request);
        }
        CookieSessionContent cookieSessionContent = new CookieSessionContent(sessionCookie.getValue());

        // check the signature
        if (!verify(cookieSessionContent.getContent(), cookieSessionContent.getSignature())) {
            return new CookieSession(request);
        }

        // now decrypt the content
        final Cipher symmetricalCipher = Cipher.getInstance(symmetricEncryptionAlgorithm);
        symmetricalCipher.init(Cipher.DECRYPT_MODE, this.symmetricEncryptionKey);
        byte[] decodedContent = symmetricalCipher.doFinal(cookieSessionContent.getContent());

        // deserialize content
        return (CookieSession) conf.asObject(decodedContent);
    }

    public void writeSession(HttpServletRequest request, HttpServletResponse response) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SparkHttpRequestWrapper sparkRequest = (SparkHttpRequestWrapper) request;
        if (!sparkRequest.sessionAccessed()) return;
        CookieSession session = (CookieSession) request.getSession();

        // serialize session
        byte[] sessionBytes = conf.asByteArray(session);

        // encrypt content
        final Cipher symmetricalCipher = Cipher.getInstance(symmetricEncryptionAlgorithm);
        symmetricalCipher.init(Cipher.ENCRYPT_MODE, this.symmetricEncryptionKey);
        byte[] encryptedBytes = symmetricalCipher.doFinal(sessionBytes);

        // sign content
        byte[] signature = sign(encryptedBytes);
        byte[] cookieContent = new byte[encryptedBytes.length + signature.length];

        System.arraycopy(encryptedBytes, 0, cookieContent, 0, encryptedBytes.length);
        System.arraycopy(signature, 0, cookieContent, cookieContent.length - signature.length, signature.length);

        String base64CookieContent = Base64.getEncoder().encodeToString(cookieContent);
        addCookie(base64CookieContent, response);
    }

    private byte[] hash(byte[] encryptedBytes) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        return instance.digest(encryptedBytes);
    }

    private void addCookie(String cookieContent, HttpServletResponse response) {
        Cookie cookie = new Cookie(CookieSessionHandler.session, cookieContent);
        cookie.setSecure(SparkBase.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    private boolean verify(byte[] dataToVerify, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA1 = Mac.getInstance("HmacSHA1");
        hmacSHA1.init(this.hmacKey);
        return Arrays.equals(hmacSHA1.doFinal(dataToVerify), signature);
    }

    private byte[] sign(byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA1 = Mac.getInstance("HmacSHA1");
        hmacSHA1.init(this.hmacKey);
        return hmacSHA1.doFinal(dataToSign);
    }

    private Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
