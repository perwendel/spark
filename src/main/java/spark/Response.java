/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
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
     * Gets the raw response object handed in by Jetty
     */
    public HttpServletResponse getRawResponse() {
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
