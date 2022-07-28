package spark.utils;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SparkUtilsTest {

    @Test
    public void testConvertRouteToList() throws Exception {

        List<String> expected = Arrays.asList("api", "person", ":id");

        List<String> actual = SparkUtils.convertRouteToList("/api/person/:id");

        assertThat("Should return route as a list of individual elements that path is made of",
            actual,
            CoreMatchers.is(expected));

    }

    @Test
    public void testIsParam_whenParameterFormattedAsParm() throws Exception {

        assertTrue("Should return true because parameter follows convention of a parameter (:paramname)",
            SparkUtils.isParam(":param"));

    }

    @Test
    public void testIsParam_whenParameterNotFormattedAsParm() throws Exception {

        assertFalse("Should return false because parameter does not follows convention of a parameter (:paramname)",
            SparkUtils.isParam(".param"));

    }

    @Test
    public void testIsSplat_whenParameterIsASplat() throws Exception {

        assertTrue("Should return true because parameter is a splat (*)", SparkUtils.isSplat("*"));

    }

    @Test
    public void testIsSplat_whenParameterIsNotASplat() throws Exception {

        assertFalse("Should return true because parameter is not a splat (*)", SparkUtils.isSplat("!"));

    }
}
