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

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.System.arraycopy;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class StaticFilesFromArchiveTest {

    private static SparkTestUtil testUtil;
    private static ClassLoader classLoader;
    private static ClassLoader initialClassLoader;

    @BeforeClass
    public static void setup() throws Exception {
        setupClassLoader();
        testUtil = new SparkTestUtil(4567);

        Class<?> sparkClass = classLoader.loadClass("spark.Spark");

        Method staticFileLocationMethod = sparkClass.getMethod("staticFileLocation", String.class);
        staticFileLocationMethod.invoke(null, "/public-jar");

        Method initMethod = sparkClass.getMethod("init");
        initMethod.invoke(null);

        Method awaitInitializationMethod = sparkClass.getMethod("awaitInitialization");
        awaitInitializationMethod.invoke(null);
    }

    @AfterClass
    public static void resetClassLoader() {
        Thread.currentThread().setContextClassLoader(initialClassLoader);
    }

    private static void setupClassLoader() throws Exception {
        ClassLoader extendedClassLoader = createExtendedClassLoader();
        initialClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(extendedClassLoader);
        classLoader = extendedClassLoader;
    }

    private static URLClassLoader createExtendedClassLoader() {
        URL[] parentURLs = ((URLClassLoader) getSystemClassLoader()).getURLs();
        URL[] urls = new URL[parentURLs.length + 1];
        arraycopy(parentURLs, 0, urls, 0, parentURLs.length);

        URL publicJar = StaticFilesFromArchiveTest.class.getResource("/public-jar.zip");
        urls[urls.length - 1] = publicJar;

        // no parent classLoader because Spark and the static resources need to be loaded from the same classloader
        return new URLClassLoader(urls, null);
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
