package spark.multipart;

import spark.utils.IOUtils;
import spark.utils.ObjectUtils;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * Encapsulates the standard Servlet multipart "Part" as the source for a Spark MultipartPart. Should Spark decide to
 * offer non-servlet based HTTP web engines then Request will need to create a different instance of MultipartPart to
 * carry the load.
 */
public class ServletMultipartPart implements MultipartPart {

    // Haha... private part. Don't act like you aren't snickering at that!
    private Part part;
    private InputStream stream;

    // Capture the various auto-converted formats if you don't use the stream directly to protect against multiple calls
    private byte[] streamBytes;
    private String streamString;

    public ServletMultipartPart(Part part) {
        this.part = part;
    }

    /**
     * This is the actual "name" of the part. This is NOT necessarily unique across multiple parts within the same
     * request as it is perfectly legal to submit multiple parts named "attachments[]". In cases like that Spark will
     * generate a separate MultipartPart instance for each one, but they'll have have the name "attachments[]".
     *
     * @return The name of the part submitted w/ the request
     */
    @Override
    public String name() {
        return part.getName();
    }

    /**
     * When constructing a multipart request, you can include the name of the original file that this part represents.
     * So if you're handing a file upload you can send "my-dogs.jpg" along with the part name and data. This field
     * is completely optional and has no "trusted" meaning.
     * <br/>
     * SECURITY WARNING: Do not use this file name as-is in your application if you are going to save this data to
     * a file system somewhere. Someone, for instance, could submit a "picture" named "/usr/bin/vi" which is actually
     * some malicious binary. If you save the filename as is, bad things are likely to happen, so if you do intend to use
     * this field for persistent paths, make sure to sanitize the file name first.
     *
     * @return The name of the original file that this part represents
     */
    @Override
    public String fileName() {
        return part.getSubmittedFileName();
    }

    /**
     * Retrieves the MIME type of the data encoded in this part.
     *
     * @return The MIME type
     */
    @Override
    public String contentType() {
        return part.getContentType();
    }

    /**
     * Retrieves the total number of bytes encoded in this part.
     *
     * @return The length of the stream in bytes
     */
    @Override
    public long contentLength() {
        return part.getSize();
    }

    /**
     * Retrieves the decoded part data as a raw stream of bytes. This stream is lazy-loaded so if you don't access the
     * stream you don't need to do anything with it. If you do access this stream it is up to you to properly close it later.
     *
     * @return The raw data stream
     */
    @Override
    public InputStream inputStream() throws IOException {
        return (stream == null)
            ? stream = part.getInputStream()
            : stream;
    }

    /**
     * Completely reads the decoded data as a UTF-8 String. The underlying stream will be closed after this operation
     * is finished.
     *
     * @return The part data as a string.
     */
    @Override
    public String asString() {
        if (streamString == null) {
            try (InputStream input = inputStream()) {
                streamString = (input == null) ? "" : IOUtils.toString(input);
            }
            catch (IOException ioe) {
                streamString = "";
            }
        }
        return streamString;
    }

    /**
     * Completely reads the decoded data as a raw byte array. The underlying stream will be closed after this operation
     * is finished.
     *
     * @return The part data as a byte array. If an error occurs you'll receive a 0-length array, not null.
     */
    @Override
    public byte[] asBytes() {
        if (streamBytes == null) {
            try (InputStream input = inputStream()) {
                streamBytes = (input == null) ? new byte[0] : IOUtils.toByteArray(input);
            }
            catch (IOException ioe) {
                streamBytes = new byte[0];
            }
        }
        return streamBytes;
    }

    /**
     * Closes our underlying data stream if we opened it and haven't yet closed it
     */
    @Override
    public void close() throws Exception {
        ObjectUtils.closeQuietly(stream);
    }
}
