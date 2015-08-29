package spark.session;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Created by Tim Heinrich on 29.08.2015.
 */
public class CookieSessionContent {
    private final byte[] content;
    private final byte[] signature;

    public CookieSessionContent(String sessionContent) {
        byte[] signatureAndContent = Base64.getDecoder().decode(sessionContent);

        // first 4 bytes are the length of the signature
        ByteBuffer byteBuffer = ByteBuffer.allocate(4).put(signatureAndContent, 0, 4);
        byteBuffer.position(0);
        int encodedSignatureLength = byteBuffer.getInt();

        signature = new byte[encodedSignatureLength];
        int currentPosition = 4;
        for (int i = 0; i < signature.length; i++) {
            signature[i] = signatureAndContent[currentPosition++];
        }
        content = new byte[signatureAndContent.length - encodedSignatureLength - 4];
        for (int i = 0; i < content.length; i ++) {
            content[i] = signatureAndContent[currentPosition++];
        }
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getSignature() {
        return signature;
    }
}
