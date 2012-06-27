package com.aeells.spark.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class RequestTransformer
{
    public String encode(final String param) throws RequestTransformException
    {
        try
        {
            return URLEncoder.encode(param, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new RequestTransformException("unable to encode request parameter: " + param);
        }
    }

    public String decode(final String param) throws RequestTransformException
    {
        try
        {
            return URLDecoder.decode(param, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new RequestTransformException("unable to decode request parameter: " + param);
        }
    }
}