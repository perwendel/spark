package spark.session;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.*;
import java.util.Base64;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class CookieSessionHandler {
    private static final String session = "session";
    private final Cipher decryptCypher;
    private final Cipher encryptCypher;
    private final Signature verifier;
    private final Signature signer;
    private final Cipher aes = Cipher.getInstance("AES");

    public CookieSessionHandler(KeyPair encryptionKeyPair, KeyPair signingKeyPair)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        verifier = Signature.getInstance(signingKeyPair.getPrivate().getAlgorithm());
        verifier.initVerify(signingKeyPair.getPublic());

        signer = Signature.getInstance(signingKeyPair.getPrivate().getAlgorithm());
        signer.initSign(signingKeyPair.getPrivate());

        decryptCypher = Cipher.getInstance(encryptionKeyPair.getPrivate().getAlgorithm());
        decryptCypher.init(Cipher.DECRYPT_MODE, encryptionKeyPair.getPrivate());

        encryptCypher = Cipher.getInstance(encryptionKeyPair.getPublic().getAlgorithm());
        encryptCypher.init(Cipher.ENCRYPT_MODE, encryptionKeyPair.getPublic());
    }

    public synchronized byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        return decryptCypher.doFinal(encrypted);
    }

    public synchronized byte[] encrypt(byte[] decrypted) throws BadPaddingException, IllegalBlockSizeException {
        return encryptCypher.doFinal(decrypted);
    }

    public CookieSession readSession(HttpServletRequest request) throws BadPaddingException, IllegalBlockSizeException, SignatureException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cookie sessionCookie = getCookie(request, CookieSessionHandler.session);
        if (sessionCookie == null) {
            return new CookieSession(request);
        }
        String sessionContent = sessionCookie.getValue();
        // get sizes
        StringBuilder s = new StringBuilder();
        int encodedSignatureLength = -1;
        int encodedPasswordLength = -1;
        int offset = 0;
        for (char c : sessionContent.toCharArray()) {
            ++offset;
            if (c == '=') {
                if (encodedPasswordLength == -1) {
                    encodedPasswordLength = Integer.parseInt(s.toString());
                } else {
                    encodedSignatureLength = Integer.parseInt(s.toString());
                    break;
                }
                s.setLength(0);
            } else {
                s.append(c);
            }
        }

        String encodedSignature = sessionContent.substring(offset + encodedPasswordLength,
                offset + encodedPasswordLength + encodedSignatureLength);
        String encodedAesKey = sessionContent.substring(offset, offset + encodedPasswordLength);
        String encodedContent = sessionContent.substring(offset + encodedPasswordLength + encodedSignatureLength);

        // check the signature
        byte[] decodedContent = Base64.getDecoder().decode(encodedContent.getBytes());
        if (!verify(decodedContent, Base64.getDecoder().decode(encodedSignature))) {
            return new CookieSession(request);
        }

        // first decode the password for decryption
        byte[] aesKey = Base64.getDecoder().decode(encodedAesKey.getBytes());
        aesKey = decrypt(aesKey);
        SecretKey aesSecretKey = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");

        // now decrypt the content
        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.DECRYPT_MODE, aesSecretKey);
        decodedContent = aes.doFinal(decodedContent);

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
        CookieSession session = (CookieSession) request.getSession();

        // serialize session
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(session);
        objectOutputStream.flush();

        // generate new secret key to sign the encryption key
        KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
        SecretKey secretKey = aesKeyGenerator.generateKey();
        aes.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = aes.doFinal(outputStream.toByteArray());
        byte[] signature = sign(encryptedBytes);

        String base64EncodedContent = Base64.getEncoder().encodeToString(encryptedBytes);
        String base64EncodedSignature = Base64.getEncoder().encodeToString(signature);
        String base64EncodedPassword = Base64.getEncoder().encodeToString(encrypt(secretKey.getEncoded()));

        addCookie(base64EncodedContent, base64EncodedPassword, base64EncodedSignature, response);
    }

    private void addCookie(String base64EncodedContent, String base64EncodedPassword,
                           String base64EncodedSignature, HttpServletResponse response) {
        String cookieContent = base64EncodedPassword.length() + "=" + base64EncodedSignature.length() + "=" +
                base64EncodedPassword + base64EncodedSignature + base64EncodedContent;
        response.addCookie(new Cookie(CookieSessionHandler.session, cookieContent));
    }

    private synchronized boolean verify(byte[] dataToVerify, byte[] signature) throws SignatureException {
        verifier.update(dataToVerify);
        return verifier.verify(signature);
    }

    private synchronized byte[] sign(byte[] dataToSign) throws SignatureException {
        signer.update(dataToSign);
        return signer.sign();
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
