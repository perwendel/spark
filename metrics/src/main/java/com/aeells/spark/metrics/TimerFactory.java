package com.aeells.spark.metrics;

import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.Timer;

import static com.yammer.metrics.Metrics.newTimer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class TimerFactory
{
    private final String timerGroupName;

    public TimerFactory(final String timerGroupName)
    {
        this.timerGroupName = timerGroupName;
    }

    public Timer createInstance()
    {
        return newTimer(new MetricName(timerGroupName, "timer", "timer"), MILLISECONDS, SECONDS);
    }
}
