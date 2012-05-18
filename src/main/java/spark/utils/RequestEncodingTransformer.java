package spark.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class RequestEncodingTransformer
{
    public String encode(final String param) throws URLEncodingException
    {
        try
        {
            return URLEncoder.encode(param, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new URLEncodingException("unable to encode request parameter: " + param);
        }
    }

    public String decode(final String param) throws URLEncodingException
    {
        try
        {
            return URLDecoder.decode(param, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new URLEncodingException("unable to decode request parameter: " + param);
        }
    }
}