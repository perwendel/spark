package com.aeells.spark.utils;

import org.springframework.beans.factory.FactoryBean;
import us.monoid.web.Resty;

final class RestyBeanFactory  implements FactoryBean
{
    private final int timeout;

    /**
     * Factory for Resty bean that does not timeout.
     */
    RestyBeanFactory()
    {
        // http://docs.oracle.com/javase/6/docs/api/java/net/URLConnection.html#setConnectTimeout(int)
        this.timeout = 0;
    }

    /**
     * Factory for Resty bean with specified timeout.
     */
    RestyBeanFactory(final int timeout)
    {
        this.timeout = timeout;
    }

    @Override public Object getObject() throws Exception
    {
        return new Resty(Resty.Option.timeout(timeout));
    }

    @Override public Class<?> getObjectType()
    {
        return Resty.class;
    }

    @Override public boolean isSingleton()
    {
        return false;
    }
}