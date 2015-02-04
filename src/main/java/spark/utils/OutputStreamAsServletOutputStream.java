/*
 * Copyright 2015 Edward Raff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.utils;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

/**
 * Simple utility class for wrapping a standard Java {@link OutputStream} as a
 * {@link ServletOutputStream}. This performs no buffering, and directly maps 
 * calls from this class to the base output stream.
 * 
 * @author Edward Raff
 */
public class OutputStreamAsServletOutputStream extends ServletOutputStream
{
    private final OutputStream out;

    public OutputStreamAsServletOutputStream(OutputStream output) throws IOException
    {
        super();
        out = output;
    }

    @Override
    public void close() throws IOException
    {
        this.out.close();
    }

    @Override
    public void flush() throws IOException
    {
        this.out.flush();
    }

    @Override
    public void write(byte b[]) throws IOException
    {
        this.out.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException
    {
        this.out.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException
    {
        this.out.write(b);
    }
}
