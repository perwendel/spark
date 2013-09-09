package spark.webserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class AsyncHandlerList extends HandlerList {
    private static final Logger LOG = Log.getLogger(AsyncHandlerList.class);
    private ExecutorService executorService;

    public AsyncHandlerList(ExecutorService executorService) {
	this.executorService = executorService;
    }

    @Override
    public void handle(final String target, final Request baseRequest,
	    final HttpServletRequest request, final HttpServletResponse response)
	    throws IOException, ServletException {
	LOG.debug("jettyhandler, handle();");
	Boolean processed = (Boolean) request.getAttribute("processed");
	if (processed == null) {
	    final Continuation continuation = ContinuationSupport
		    .getContinuation(request);
	    if (continuation.isExpired()) {
		baseRequest.setHandled(false);
		return;
	    }
	    continuation.setTimeout(30000);
	    continuation.suspend();
	    executorService.submit(new Runnable() {
		@Override
		public void run() {
		    try {
			Handler[] handlers = getHandlers();
			if (handlers != null && isStarted()) {
			    for (int i = 0; i < handlers.length; i++) {
				handlers[i].handle(target, baseRequest,
					request, response);
				if (baseRequest.isHandled())
				    return;
			    }
			}
		    } catch (IOException | ServletException e) {
			baseRequest.setHandled(false);
		    } finally {
			synchronized (request) {
			    request.setAttribute("processed", Boolean.TRUE);
			    continuation.complete();
			}
		    }
		}
	    });
	}
    }
}
