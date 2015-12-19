package spark.utils;

import org.junit.Test;
import spark.Spark;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SparkUtilsTest {

    @Test
    public void testConvertRouteToList() throws Exception {

        List<String> expected = Arrays.asList("api", "person", ":id");

        List<String> actual = SparkUtils.convertRouteToList("/api/person/:id");

        assertThat(actual, is(expected));

    }

    @Test
    public void testIsParam() throws Exception {

        assertTrue(SparkUtils.isParam(":param"));

    }


    @Test
    public void testIsSplat() throws Exception {

        assertTrue(SparkUtils.isSplat("*"));

    }
}