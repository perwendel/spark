package com.aeells.spark.utils;

import spark.Request;
import spark.Response;

public interface SparkHandler
{
    Object handle(final Request request, final Response response);
}