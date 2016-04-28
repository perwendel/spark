package spark.extensions;

import org.junit.Test;

/**
 * @author David Åse
 */
public class ExtensionTest {

    @Test
    public void testExtensions() {
        ExtendedSpark.get("/", () -> "Hello"); //if it compiles it works ... :)
        ExtendedSpark.stop();
    }

}
