package spark.webserver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.Access;
import spark.HaltException;
import spark.InterceptorChain;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.route.RouteMatch;

public class SimpleInterceptorChain implements InterceptorChain {

    private org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

    private List<RouteMatch> interceptors;
    private TargetInvocation targetInvocation;
    private int index;
    private String bodyContent;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public SimpleInterceptorChain(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            TargetInvocation targetInvocation, List<RouteMatch> interceptors) {
        super();
        this.targetInvocation = targetInvocation;
        this.interceptors = interceptors;
    }
    
    public String getBodyContent() {
        return bodyContent;
    }

    @Override
    public void invokeNext() {
        try {
            if (index == interceptors.size()) {
                LOG.debug("Final link in the chain: invoking target");
                invokeTarget();
                return;
            }
    
            try {
                RouteMatch interceptor = interceptors.get(index++);
                invokeInterceptor(interceptor);
            } finally {
                index--;
            }
        }
        catch(HaltException he) {
            throw he;
        }
        catch(RuntimeException re) {
            LOG.error("Oops", re);
            throw re;
        }
    }

    private void invokeTarget() {
        bodyContent = targetInvocation.invokeTargetMethod(bodyContent);
    }

    private void invokeInterceptor(RouteMatch match) {
        RequestWrapper req = new RequestWrapper();
        ResponseWrapper res = new ResponseWrapper();

        Object filterTarget = match.getTarget();
        if (filterTarget != null && filterTarget instanceof spark.Interceptor) {
            Request request = RequestResponseFactory.create(match, httpRequest);
            Response response = RequestResponseFactory.create(httpResponse);

            req.setDelegate(request);
            res.setDelegate(response);

            spark.Interceptor interceptor = (spark.Interceptor) filterTarget;
            interceptor.handle(req, res, this);

            String bodyAfterFilter = Access.getBody(response);
            if (bodyAfterFilter != null) {
                bodyContent = bodyAfterFilter;
            }
        }
    }

}
