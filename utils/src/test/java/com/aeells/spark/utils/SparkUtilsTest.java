package com.aeells.spark.utils;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class SparkUtilsTest
{
    private final SparkUtils sparkUtils = new SparkUtils();

    @Test
    public void simpleUnpackPostBody() throws RequestTransformException
    {
        final Map<String, String> keyValuePairs = sparkUtils.unpackPostBody("abc=123&xyz=789");

        assertThat(keyValuePairs.get("abc"), equalTo("123"));
        assertThat(keyValuePairs.get("xyz"), equalTo("789"));
    }

    @Test
    public void ensureEncodedBodyParametersDecoded() throws RequestTransformException
    {
        final Map<String, String> keyValuePairs = sparkUtils.unpackPostBody("abc=1%201&xyz=1%251");

        assertThat(keyValuePairs.get("abc"), equalTo("1 1"));
        assertThat(keyValuePairs.get("xyz"), equalTo("1%1"));
    }
}