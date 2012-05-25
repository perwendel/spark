package com.aeells.spark.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public final class SparkUtils
{
    public Map<String, String> unpackPostBody(final String postBody) throws RequestTransformException
    {
        try
        {
            final Map<String, String> keyValuePairs = new HashMap<String, String>();

            for (final String keyValuePair : StringUtils.split(URLDecoder.decode(postBody, "UTF-8"), "&"))
            {
                keyValuePairs.put(StringUtils.substringBefore(keyValuePair, "="), StringUtils.substringAfter(keyValuePair, "="));
            }

            return keyValuePairs;
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new RequestTransformException("unable to parse request body: " + postBody);
        }
    }
}
