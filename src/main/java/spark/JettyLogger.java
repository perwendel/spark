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

import org.mortbay.log.Logger;

/**
 * 
 *
 * @author Per Wendel
 */
public class JettyLogger implements Logger {

    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger("org.mortbay.jetty.JettyLog");
    
    public void debug(String msg, Throwable th) {
        LOG.debug(msg, th);
    }

    public void debug(String msg, Object arg0, Object arg1)  {
        LOG.debug(msg + ", " + arg0 + ", " + arg1);
    }

    public Logger getLogger(String arg0) {
        return this;
    }

    public void info(String msg, Object arg0, Object arg1) {
        LOG.info(msg + ", " + arg0 + ", " + arg1);
    }

    public boolean isDebugEnabled() {
        return LOG.isDebugEnabled();
    }

    public void warn(String msg, Throwable th) {
        LOG.warn(msg, th);
    }

    public void warn(String msg, Object arg0, Object arg1)  {
        LOG.warn(msg + ", " + arg0 + ", " + arg1);
    }

}
