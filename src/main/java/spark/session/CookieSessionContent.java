package spark.session;

/**
 * Created by Tim Heinrich on 29.08.2015.
 */
public class CookieSessionContent {
    private final String encodedSymmetricalKey;
    private final String encodedContent;
    private final String encodedSignature;

    public CookieSessionContent(String sessionContent) {
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

        encodedSignature = sessionContent.substring(offset + encodedPasswordLength,
                offset + encodedPasswordLength + encodedSignatureLength);
        encodedSymmetricalKey = sessionContent.substring(offset, offset + encodedPasswordLength);
        encodedContent = sessionContent.substring(offset + encodedPasswordLength + encodedSignatureLength);

    }

    public String getEncodedSymmetricalKey() {
        return encodedSymmetricalKey;
    }

    public String getEncodedContent() {
        return encodedContent;
    }

    public String getEncodedSignature() {
        return encodedSignature;
    }
}
