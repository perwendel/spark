package com.aeells.spark.metrics;

import com.aeells.spark.utils.SparkHandler;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import spark.Request;
import spark.Response;

public final class TimedHandlerDecorator implements SparkHandler
{
    private final SparkHandler sparkHandler;

    private final Timer timer;

    public TimedHandlerDecorator(final SparkHandler sparkHandler, final Timer timer)
    {
        this.sparkHandler = sparkHandler;
        this.timer = timer;
    }

    public Object handle(final Request request, final Response response)
    {
        final TimerContext context = timer.time();
        try
        {
            return sparkHandler.handle(request, response);
        }
        finally
        {
            context.stop();
        }
    }
}
