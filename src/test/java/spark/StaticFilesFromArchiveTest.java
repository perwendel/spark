/*
 * Copyright 2016 - Per Wendel
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
package spark;

import static java.lang.System.arraycopy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.examples.exception.NotFoundException;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class StaticFilesFromArchiveTest {

    private static SparkTestUtil testUtil;

    @BeforeClass
    public static void setup() throws Exception {
        testUtil = new SparkTestUtil(4567);

        ClassLoader classLoader = getClassLoader();
        Class<?> sparkClass = classLoader.loadClass("spark.Spark");

        Method staticFileLocationMethod = sparkClass.getMethod("staticFileLocation", String.class);
        staticFileLocationMethod.invoke(null, "/public-jar");

        Method initMethod = sparkClass.getMethod("init");
        initMethod.invoke(null);

        Method awaitInitializationMethod = sparkClass.getMethod("awaitInitialization");
        awaitInitializationMethod.invoke(null);
    }

    protected static ClassLoader getClassLoader() throws Exception {
        URL[] parentURLs = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        URL[] urls = new URL[parentURLs.length + 1];
        arraycopy(parentURLs, 0, urls, 0, parentURLs.length);
        URL publicJar = StaticFilesFromArchiveTest.class.getResource("/public-jar.zip");
        urls[parentURLs.length] = publicJar;

        URLClassLoader urlClassLoader = new URLClassLoader(urls, null);
        Thread.currentThread().setContextClassLoader(urlClassLoader);
        return urlClassLoader;
    }

    @Test
    public void testCss() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/css/style.css", null);

        String expectedContentType = response.headers.get("Content-Type");
        assertEquals(expectedContentType, "text/css");

        String body = response.body;
        assertEquals("Content of css file", body);
    }
}
