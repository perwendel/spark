package spark.webserver;

import spark.utils.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
class HttpRequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedBytes;

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }
        return new CachedServletInputStream();
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = IOUtils.toByteArray(super.getInputStream());
    }

    public class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream byteArrayInputStream;

        public CachedServletInputStream() {
            byteArrayInputStream = new ByteArrayInputStream(cachedBytes);
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() <= 0;
        }

        @Override
        public boolean isReady() {
            return byteArrayInputStream.available() >= 0;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
