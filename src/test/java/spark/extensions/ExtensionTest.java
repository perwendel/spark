package spark.extensions;

import org.junit.Test;

import static spark.extensions.ExtendedService.ignite;

/**
 * @author David Åse
 */
public class ExtensionTest {

    @Test
    public void testExtensions() { //if it compiles it works ... :)

        ExtendedSpark.get("/", () -> "Hello");
        ExtendedSpark.stop();

        ExtendedService extendedHttp = ignite().port(1234).staticFileLocation("/public");
        extendedHttp.get("/", () -> "Hello");
        extendedHttp.stop();

    }

}
