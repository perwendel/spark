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
package spark.webserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import spark.utils.OutputStreamAsServletOutputStream;

/**
 * Wraps the Servlet Response such that it is compressed using 
 * {@link GZIPOutputStream}
 * 
 * @author Edward Raff
 */
public class GZIPServletResponseWrapper extends HttpServletResponseWrapper
{

    private GZIPOutputStream gzipOut = null;
    private PrintWriter printOut = null;

    public GZIPServletResponseWrapper(HttpServletResponse response) throws IOException
    {
        super(response);
    }

    public void close() throws IOException
    {
        if(printOut != null)
            printOut.close();//wraps gzip, so call first
        else if(gzipOut != null)
            gzipOut.close();
    }
    
    @Override
    public void flushBuffer() throws IOException
    {
        if(printOut != null)
            printOut.flush();//wraps gzip, so call first
        else if(gzipOut != null)
            gzipOut.flush();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (this.printOut != null || gzipOut != null)
            throw new IllegalStateException("An output stream has already been obtained"); 
        gzipOut = new GZIPOutputStream(getResponse().getOutputStream());
        
        return new OutputStreamAsServletOutputStream(gzipOut);
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        return printOut = new PrintWriter(getOutputStream());
    }

    @Override
    public void setContentLength(int len)
    {
        //do I need to set this since gzip will change the length of the stream?
    }
}
