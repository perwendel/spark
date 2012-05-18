/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some utility methods
 *
 * @author Per Wendel
 */
public class SparkUtils {

    public static final String ALL_PATHS = "+/*paths";

    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }

    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }

    public Map<String, String> unpackPostBody(final String postBody) throws URLEncodingException
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
            throw new URLEncodingException("unable to parse request body: " + postBody);
        }
    }
}
