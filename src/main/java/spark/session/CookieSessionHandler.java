package spark.session;

import org.nustaq.serialization.FSTConfiguration;
import spark.SparkBase;
import spark.webserver.SparkHttpRequestWrapper;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class CookieSessionHandler {
    private static final String session = "session";
    private static final String symmetricEncryptionAlgorithm = "AES";
    private static final String hashFunction = "SHA1";
    private static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    private final String signatureAlgorithm;
    private final SecretKey symmetricEncryptionKey;
    private final KeyPair encryptionKeyPair;
    private final Cipher symmetricalCipher;

    static {
        conf.setPreferSpeed(true);
    }

    public CookieSessionHandler(KeyPair encryptionKeyPair, String symmetricEncryptionKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        this.encryptionKeyPair = encryptionKeyPair;
        this.signatureAlgorithm = hashFunction + "with" + encryptionKeyPair.getPublic().getAlgorithm();

        byte[] key = hash(symmetricEncryptionKey.getBytes());
        key = Arrays.copyOf(key, 16);
        this.symmetricEncryptionKey = new SecretKeySpec(key, symmetricEncryptionAlgorithm);

        symmetricalCipher = Cipher.getInstance(symmetricEncryptionAlgorithm);
    }

    public CookieSession readSession(HttpServletRequest request) throws BadPaddingException, IllegalBlockSizeException, SignatureException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cookie sessionCookie = getCookie(request, CookieSessionHandler.session);
        if (sessionCookie == null) {
            return new CookieSession(request);
        }
        CookieSessionContent cookieSessionContent = new CookieSessionContent(sessionCookie.getValue());

        // check the signature
        if (!verify(hash(cookieSessionContent.getContent()), cookieSessionContent.getSignature())) {
            return new CookieSession(request);
        }

        // now decrypt the content
        symmetricalCipher.init(Cipher.DECRYPT_MODE, this.symmetricEncryptionKey);
        byte[] decodedContent = symmetricalCipher.doFinal(cookieSessionContent.getContent());

        // deserialize content
        return (CookieSession) conf.asObject(decodedContent);
    }

    public void writeSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException, BadPaddingException, IllegalBlockSizeException, SignatureException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SparkHttpRequestWrapper sparkRequest = (SparkHttpRequestWrapper) request;
        if (!sparkRequest.sessionAccessed()) return;
        CookieSession session = (CookieSession) request.getSession();

        // serialize session
        byte[] sessionBytes = conf.asByteArray(session);

        // encrypt content
        symmetricalCipher.init(Cipher.ENCRYPT_MODE, this.symmetricEncryptionKey);
        byte[] encryptedBytes = symmetricalCipher.doFinal(sessionBytes);

        // sign content
        byte[] signature = sign(hash(encryptedBytes));

        byte[] cookieContent = new byte[4 + encryptedBytes.length + signature.length];
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(signature.length);
        buffer.position(0);
        buffer.get(cookieContent, 0, 4);
        int bytePosition = 4;
        for (int i = 0; i < signature.length; i++) {
            cookieContent[bytePosition++] = signature[i];
        }
        for (int i = 0; i < encryptedBytes.length; i++) {
            cookieContent[bytePosition++] = encryptedBytes[i];
        }

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

    private boolean verify(byte[] dataToVerify, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature verifier = Signature.getInstance(signatureAlgorithm);
        verifier.initVerify(encryptionKeyPair.getPublic());
        verifier.update(dataToVerify);
        return verifier.verify(signature);
    }

    private byte[] sign(byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature instance = Signature.getInstance(signatureAlgorithm);
        instance.initSign(encryptionKeyPair.getPrivate());
        instance.update(dataToSign);
        return instance.sign();
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
