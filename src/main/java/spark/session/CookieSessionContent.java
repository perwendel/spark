package spark.session;

/**
 * Created by Tim Heinrich on 29.08.2015.
 */
public class CookieSessionContent {
    private final String encodedContent;
    private final String encodedSignature;

    public CookieSessionContent(String sessionContent) {
        int offset = 0;
        char[] buffer = new char[5];
        char c;
        while ((c = sessionContent.charAt(offset)) != '=') {
            buffer[offset++] = c;
        }
        int encodedSignatureLength = Integer.parseInt(new String(buffer, 0, offset++));

        encodedSignature = sessionContent.substring(offset, offset + encodedSignatureLength);
        encodedContent = sessionContent.substring(offset + encodedSignatureLength);
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    public String getEncodedSignature() {
        return encodedSignature;
    }
}
