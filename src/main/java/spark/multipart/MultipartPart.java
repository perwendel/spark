package spark.multipart;

import java.io.IOException;
import java.io.InputStream;

/**
 * When using HTTP(S) to upload files to the web server you typically encode the binary data as a "multipart" request
 * where the Content-Type is something like "multipart/XXX; YYY=ZZZ". Most web frameworks like Jetty support
 * abstractions to pick decode and pick apart these "parts" of the request.
 * <br/>
 * A MultipartPart encapsulates a single part of the multipart request, standardizing how you access things such as the
 * name of the part, its content type, and the raw data for that part. Regardless of the underlying web server Spark
 * will provide these standard accessors for the multipart data.
 * <br/>
 * This interface is AutoCloseable so that you can use your parts in Try-With-Resources blocks to safely clean up any
 * streams/resources associated with this Blob of data.
 */
public interface MultipartPart extends AutoCloseable {

    /**
     * This is the actual "name" of the part. This is NOT necessarily unique across multiple parts within the same
     * request as it is perfectly legal to submit multiple parts named "attachments[]". In cases like that Spark will
     * generate a separate MultipartPart instance for each one, but they'll have have the name "attachments[]".
     *
     * @return The name of the part submitted w/ the request
     */
    String name();

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
    String fileName();

    /**
     * Retrieves the MIME type of the data encoded in this part.
     *
     * @return The MIME type
     */
    String contentType();

    /**
     * Retrieves the total number of bytes encoded in this part.
     *
     * @return The length of the stream in bytes
     */
    long contentLength();

    /**
     * Retrieves the decoded part data as a raw stream of bytes. This stream is lazy-loaded so if you don't access the
     * stream you don't need to do anything with it. If you do access this stream it is up to you to properly close it later.
     *
     * @return The raw data stream
     */
    InputStream inputStream() throws IOException;

    /**
     * Completely reads the decoded data as a UTF-8 String. The underlying stream will be closed after this operation
     * is finished.
     *
     * @return The part data as a string.
     */
    String asString();

    /**
     * Completely reads the decoded data as a raw byte array. The underlying stream will be closed after this operation
     * is finished.
     *
     * @return The part data as a byte array.
     */
    byte[] asBytes();
}
