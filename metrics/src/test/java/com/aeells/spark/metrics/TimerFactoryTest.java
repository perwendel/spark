package com.aeells.spark.metrics;

import com.yammer.metrics.core.Timer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public final class TimerFactoryTest
{
    private final TimerFactory timerFactory = new TimerFactory("whatever");

    @Test
    public void timerCreatedByFactoryIsNotCurrentlyConfigurableButThisCouldBeExtended()
    {
        final Timer timer = timerFactory.createInstance();

        assertThat(timer.durationUnit(), equalTo(TimeUnit.MILLISECONDS));
        assertThat(timer.rateUnit(), equalTo(TimeUnit.SECONDS));
    }
}
