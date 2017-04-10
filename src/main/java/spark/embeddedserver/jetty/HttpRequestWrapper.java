/*
 * Copyright 2015 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.embeddedserver.jetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import spark.utils.IOUtils;

/**
 * Http request wrapper. Wraps the request so 'getInputStream()' can be called multiple times.
 * Also has methods for checking if request has been consumed.
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {
    private byte[] cachedBytes;
    private boolean notConsumed = false;

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public boolean notConsumed() {
        return notConsumed;
    }

    public void notConsumed(boolean notConsumed) {
        this.notConsumed = notConsumed;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        String transferEncoding = ((HttpServletRequest) super.getRequest()).getHeader("Transfer-Encoding");
        if ("chunked".equals(transferEncoding)) {
            return super.getInputStream(); // disable stream cache for chunked transfer encoding
        }
        if (cachedBytes == null) {
            cacheInputStream();
        }
        return new CachedServletInputStream();
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = IOUtils.toByteArray(super.getInputStream());
    }

    private class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream byteArrayInputStream;

        public CachedServletInputStream() {
            byteArrayInputStream = new ByteArrayInputStream(cachedBytes);
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }

        @Override
        public int available() {
            return byteArrayInputStream.available();
        }

        @Override
        public boolean isFinished() {
            return available() <= 0;
        }

        @Override
        public boolean isReady() {
            return available() >= 0;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}