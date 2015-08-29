package spark.session;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by Tim Heinrich on 29.08.2015.
 */
public class CookieSessionContent {
    // last 20 bytes are the length of the signature
    private static final int encodedSignatureLength = 20;

    private final byte[] content;
    private final byte[] signature;

    public CookieSessionContent(String sessionContent) {
        byte[] signatureAndContent = Base64.getDecoder().decode(sessionContent);

        signature = new byte[encodedSignatureLength];
        content = new byte[signatureAndContent.length - encodedSignatureLength];

        System.arraycopy(signatureAndContent, 0, content, 0, content.length);
        System.arraycopy(signatureAndContent, signatureAndContent.length - signature.length,
                signature, 0, signature.length);
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getSignature() {
        return signature;
    }
}
