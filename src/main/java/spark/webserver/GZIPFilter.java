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
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple filter for using gzip to compress a response if the client indicates 
 * that they support Gzip compression
 * 
 * @author Edward Raff
 */
public class GZIPFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        //should we be initing here? Spark seems to ingore these
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
         HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if(Collections.list(httpRequest.getHeaders("Accept-Encoding")).contains("gzip"))
        {
            httpResponse.addHeader("Content-Encoding", "gzip");
            GZIPServletResponseWrapper gzipResponse = new GZIPServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy()
    {
        
    }
    
}
