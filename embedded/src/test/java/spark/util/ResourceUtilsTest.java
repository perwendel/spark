package spark.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import spark.utils.ResourceUtils;

import static org.junit.Assert.assertEquals;

public class ResourceUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetFile_whenURLProtocolIsNotFile_thenThrowFileNotFoundException() throws
                                                                                      MalformedURLException,
                                                                                      FileNotFoundException {
        thrown.expect(FileNotFoundException.class);
        thrown.expectMessage("My File Path cannot be resolved to absolute file path " +
                                     "because it does not reside in the file system: http://example.com/");

        URL url = new URL("http://example.com/");
        ResourceUtils.getFile(url, "My File Path");
    }

    @Test
    public void testGetFile_whenURLProtocolIsFile_thenReturnFileObject() throws
                                                                         MalformedURLException,
                                                                         FileNotFoundException,
                                                                         URISyntaxException {
        //given
        URL url = new URL("file://public/file.txt");
        File file = ResourceUtils.getFile(url, "Some description");

        //then
        assertEquals("Should be equals because URL protocol is file", file, new File(ResourceUtils.toURI(url).getSchemeSpecificPart()));
    }

}
