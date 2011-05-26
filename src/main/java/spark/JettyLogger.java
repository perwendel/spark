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

import org.eclipse.jetty.util.log.Logger;

/**
 * Jetty Logger
 *
 * @author Per Wendel
 */
public class JettyLogger implements Logger {

    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger("org.mortbay.jetty.JettyLog");

    public void debug(String msg, Throwable th) {
        LOG.debug(msg, th);
    }

    public Logger getLogger(String arg) {
        return this;
    }

    @Override
    public boolean isDebugEnabled() {
        return LOG.isDebugEnabled();
    }

    @Override
    public void warn(String msg, Throwable th) {
        LOG.warn(msg, th);
    }

    @Override
    public void debug(Throwable thrown) {
        LOG.debug("", thrown);

    }

    @Override
    public void debug(String msg, Object... args) {
        StringBuffer log = new StringBuffer(msg);
        for (Object arg : args) {
            log.append(", " + arg);

        }
        LOG.debug(log.toString());
    }

    @Override
    public String getName() {
        return "Spark Jetty Logger";
    }

    @Override
    public void ignore(Throwable ignored) {
        //
    }

    @Override
    public void info(Throwable thrown) {
        LOG.info("", thrown);
    }

    @Override
    public void info(String msg, Object... args) {
        StringBuffer log = new StringBuffer(msg);
        for (Object arg : args) {
            log.append(", " + arg);

        }
        LOG.info(log.toString());
    }

    @Override
    public void info(String msg, Throwable thrown) {
        LOG.info(msg, thrown);
    }

    @Override
    public void setDebugEnabled(boolean enabled) {
        // 
    }

    @Override
    public void warn(Throwable thrown) {
        LOG.warn("", thrown);
    }

    @Override
    public void warn(String msg, Object... args) {
        StringBuffer log = new StringBuffer(msg);
        for (Object arg : args) {
            log.append(", " + arg);

        }
        LOG.warn(log.toString());
    }

}
