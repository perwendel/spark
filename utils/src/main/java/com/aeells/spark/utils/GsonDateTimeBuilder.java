package com.aeells.spark.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public final class GsonDateTimeBuilder
{
    public Gson serialize()
    {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer()).create();
    }

    public Gson deserialize()
    {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeDeserializer()).create();
    }

    private class DateTimeSerializer implements JsonSerializer<DateTime>
    {
        public JsonElement serialize(final DateTime src, final Type typeOfSrc, final JsonSerializationContext context)
        {
            return new JsonPrimitive(src.toString());
        }
    }

    private class DateTimeDeserializer implements JsonDeserializer<DateTime>
    {
        public DateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException
        {
            return new DateTime(json.getAsJsonPrimitive().getAsString());
        }
    }
}
