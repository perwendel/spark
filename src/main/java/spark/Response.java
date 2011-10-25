/*
 * Copyright 2011- Per Wendel
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
package spark;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Provides functionality for modifying the response
 *
 * @author Per Wendel
 */
public class Response {

    private HttpServletResponse response;
    private String body;
    
    protected Response() {
       // Used by wrapper
    }
    
    Response(HttpServletResponse response) {
        this.response = response;
    }
    
    
    /**
     * Sets the status code for the response
     */
    public void status(int statusCode) {
        response.setStatus(statusCode);
    }
    
    /**
     * Sets the content type for the response
     */
    public void type(String contentType) {
        response.setContentType(contentType);
    }
    
    /**
     * Sets the body
     */
    public void body(String body) {
       this.body = body;
    }
    
    public String body() {
       return this.body;
    }
    
    /**
     * Gets the raw response object handed in by Jetty
     */
    public HttpServletResponse raw() {
        return response;
    }

    /**
     *  Trigger a browser redirect
     * 
     * @param location Where to redirect
     */
    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds/Sets a response header
     */
    public void header(String header, String value) {
        response.addHeader(header, value);
    }
}
