package spark.session;

import spark.SparkBase;
import spark.webserver.SparkHttpRequestWrapper;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

    private final String signatureAlgorithm;
    private final SecretKey symmetricEncryptionKey;
    private final KeyPair encryptionKeyPair;

    public CookieSessionHandler(KeyPair encryptionKeyPair, String symmetricEncryptionKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.encryptionKeyPair = encryptionKeyPair;
        this.signatureAlgorithm = hashFunction + "with" + encryptionKeyPair.getPublic().getAlgorithm();

        byte[] key = hash(symmetricEncryptionKey.getBytes());
        key = Arrays.copyOf(key, 16);
        this.symmetricEncryptionKey = new SecretKeySpec(key, symmetricEncryptionAlgorithm);
    }

    public byte[] decrypt(byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher instance = Cipher.getInstance(encryptionKeyPair.getPrivate().getAlgorithm());
        instance.init(Cipher.DECRYPT_MODE, encryptionKeyPair.getPrivate());
        return instance.doFinal(encrypted);

    }

    public synchronized byte[] encrypt(byte[] decrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher instance = Cipher.getInstance(encryptionKeyPair.getPublic().getAlgorithm());
        instance.init(Cipher.ENCRYPT_MODE, encryptionKeyPair.getPublic());
        return instance.doFinal(decrypted);
    }

    public CookieSession readSession(HttpServletRequest request) throws BadPaddingException, IllegalBlockSizeException, SignatureException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cookie sessionCookie = getCookie(request, CookieSessionHandler.session);
        if (sessionCookie == null) {
            return new CookieSession(request);
        }
        String sessionContent = sessionCookie.getValue();
        CookieSessionContent cookieSessionContent = new CookieSessionContent(sessionContent);

        // check the signature
        byte[] decodedContent = Base64.getDecoder().decode(cookieSessionContent.getEncodedContent().getBytes());
        if (!verify(hash(decodedContent), Base64.getDecoder().decode(cookieSessionContent.getEncodedSignature()))) {
            return new CookieSession(request);
        }

        // first decode the password for decryption
        byte[] symmetricalKey = Base64.getDecoder().decode(cookieSessionContent.getEncodedSymmetricalKey().getBytes());
        symmetricalKey = decrypt(symmetricalKey);
        SecretKey symmetricalSecretKey =
                new SecretKeySpec(symmetricalKey, 0, symmetricalKey.length, symmetricEncryptionAlgorithm);

        // now decrypt the content
        Cipher symmetricalCipher = Cipher.getInstance(symmetricEncryptionAlgorithm);
        symmetricalCipher.init(Cipher.DECRYPT_MODE, symmetricalSecretKey);
        decodedContent = symmetricalCipher.doFinal(decodedContent);

        // deserialize content
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedContent);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            CookieSession session = (CookieSession) objectInputStream.readObject();
            session.setRequest(request);
            return session;
        } catch (Exception e) {
            return new CookieSession(request);
        }
    }

    public void writeSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException, BadPaddingException, IllegalBlockSizeException, SignatureException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SparkHttpRequestWrapper sparkRequest = (SparkHttpRequestWrapper) request;
        if (!sparkRequest.sessionAccessed()) return;
        CookieSession session = (CookieSession) request.getSession();

        // serialize session
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(session);
        objectOutputStream.flush();

        // encrypt content
        Cipher symmetricalCipher = Cipher.getInstance(symmetricEncryptionAlgorithm);
        symmetricalCipher.init(Cipher.ENCRYPT_MODE, symmetricEncryptionKey);
        byte[] encryptedBytes = symmetricalCipher.doFinal(outputStream.toByteArray());

        // sign content
        byte[] signature = sign(hash(encryptedBytes));

        // encode result
        String base64EncodedContent = Base64.getEncoder().encodeToString(encryptedBytes);
        String base64EncodedSignature = Base64.getEncoder().encodeToString(signature);
        String base64EncodedPassword = Base64.getEncoder().encodeToString(encrypt(symmetricEncryptionKey.getEncoded()));

        addCookie(base64EncodedContent, base64EncodedPassword, base64EncodedSignature, response);
    }

    private byte[] hash(byte[] encryptedBytes) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        return instance.digest(encryptedBytes);
    }

    private void addCookie(String base64EncodedContent, String base64EncodedPassword,
                           String base64EncodedSignature, HttpServletResponse response) {
        String cookieContent = base64EncodedPassword.length() + "=" + base64EncodedSignature.length() + "=" +
                base64EncodedPassword + base64EncodedSignature + base64EncodedContent;

        Cookie cookie = new Cookie(CookieSessionHandler.session, cookieContent);
        cookie.setSecure(SparkBase.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    private synchronized boolean verify(byte[] dataToVerify, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature instance = Signature.getInstance(signatureAlgorithm);
        instance.initVerify(encryptionKeyPair.getPublic());
        instance.update(dataToVerify);
        return instance.verify(signature);
    }

    private synchronized byte[] sign(byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
