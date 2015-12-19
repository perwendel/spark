package spark.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class MimeParseTest {

    @Test
    public void testBestMatch() throws Exception {

        final String header = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

        Collection<String> supported = Arrays.asList("application/xml", "text/html");

        assertEquals("text/html", MimeParse.bestMatch(supported, header));

    }

    @Test
    public void testBestMatch_whenSupportedIsLowQualityFactor() throws Exception {

        final String header = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

        Collection<String> supported = Arrays.asList("application/json");

        assertEquals("application/json", MimeParse.bestMatch(supported, header));

    }

}