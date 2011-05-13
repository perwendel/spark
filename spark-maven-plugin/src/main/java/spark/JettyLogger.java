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

import org.mortbay.log.Logger;

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
