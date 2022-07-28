package spark.examples.gzip;

import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Per Wendel on 2015-11-24.
 */
public class GzipClient {

    public static String getAndDecompress(String url) throws Exception {
        InputStream compressed = get(url);
        GZIPInputStream gzipInputStream = new GZIPInputStream(compressed);
        String decompressed = IOUtils.toString(gzipInputStream);
        return decompressed;
    }

    public static InputStream get(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("Accept-Encoding", "gzip");
        connection.connect();

        return (InputStream) connection.getInputStream();
    }

}
