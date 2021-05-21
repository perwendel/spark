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
package spark.embeddedserver.jetty;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 * Simple Jetty Handler
 *
 * @author Per Wendel
 */
public class JettyHandler extends SessionHandler {

    //CS304 Issue link: https://github.com/perwendel/spark/issues/986
    private Set<String> consume;
    private final Filter filter;

    public JettyHandler(Filter filter) {
        this.filter = filter;
    }

    /**
     *  Handle the request
     *
     * @param target        Target host
     * @param baseRequest   BaseRequest
     * @param request       Request in HttpServletRequest
     * @param response      Response in HttpServletResponse
     *
     * @throws IOException
     * @throws ServletException
     */
    //CS304 Issue link: https://github.com/perwendel/spark/issues/1069
    //CS304 Issue link: https://github.com/perwendel/spark/issues/986
    @Override
    public void doHandle(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        HttpRequestWrapper wrapper = new HttpRequestWrapper(request);
        if(consume!=null && consume.contains(baseRequest.getRequestURI())){
            wrapper.notConsumed(true);
        }
        else {
            filter.doFilter(wrapper, response, null);
        }

        baseRequest.setHandled(!wrapper.notConsumed());

    }

    //CS304 Issue link: https://github.com/perwendel/spark/issues/986
    public void consume(Set<String> consume){
        this.consume=consume;
    }

    //CS304 Issue link: https://github.com/perwendel/spark/issues/986
    public Set<String> consume(){
        return this.consume;
    }

}
