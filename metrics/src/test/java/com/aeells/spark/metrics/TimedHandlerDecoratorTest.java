package com.aeells.spark.metrics;

import com.aeells.spark.utils.SparkHandler;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class TimedHandlerDecoratorTest
{
    private final SparkHandler sparkHandler = mock(SparkHandler.class);

    private final Timer timer = mock(Timer.class);

    private final TimerContext timerContext = mock(TimerContext.class);

    private final TimedHandlerDecorator decorator = new TimedHandlerDecorator(sparkHandler, timer);

    @Test
    public void assertTimingSteps()
    {
        final Request request = mock(Request.class);
        final Response response = mock(Response.class);
        when(timer.time()).thenReturn(timerContext);

        decorator.handle(request, response);

        verify(timer).time();
        verify(sparkHandler).handle(request, response);
        verify(timerContext).stop();
    }
}
