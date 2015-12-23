package spark.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import spark.utils.ResourceUtils;

public class ResourceUtilsTest {
	
	@Test(expected=FileNotFoundException.class)
	public void testGetFile_whenURLProtocolIsNotFile_thenThrowFileNotFoundException() throws MalformedURLException, FileNotFoundException {
		URL url = new URL("http://example.com/");
		ResourceUtils.getFile(url, "Some description");
	}
	
	@Test
	public void testGetFile_whenURLProtocolIsFile_thenReturnFileObject() throws MalformedURLException, FileNotFoundException, URISyntaxException {
		URL url = new URL("file://public/file.txt");
		File file = ResourceUtils.getFile(url, "Some description");
		assertEquals(file, new File(ResourceUtils.toURI(url).getSchemeSpecificPart()));
	}

}
