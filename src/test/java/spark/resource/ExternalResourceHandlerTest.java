package spark.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ExternalResourceHandler.class})
public class ExternalResourceHandlerTest {

	public ExternalResourceHandlerTest() {}
	
	@Test(expected=MalformedURLException.class)
	public void testGetResource_whenPathIsNull_thenThrowMalformedURLException() throws MalformedURLException {
		String path=null;
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
		externalResourceHandler.getResource(path);
	}
	
	@Test(expected=MalformedURLException.class)
	public void testGetResource_whenPathDoesNotStartWithSlash_thenThrowMalformedURLException() throws MalformedURLException {
		String path="folder";
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
		externalResourceHandler.getResource(path);
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNotNull_thenReturnExternalResourceObject() throws Exception {
		ExternalResource firstResourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(firstResourceMock);
		
		ExternalResource secondResourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder/index.html").thenReturn(secondResourceMock);
		
		doReturn(true).when(firstResourceMock).exists();
		doReturn(true).when(firstResourceMock).isDirectory();
		PowerMockito.doReturn("/public/folder").when(firstResourceMock).getPath();
		doReturn(true).when(secondResourceMock).exists();
		PowerMockito.doReturn("/public/folder/index.html").when(secondResourceMock).getPath();
		
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
		String returnedPath = ((ExternalResource) externalResourceHandler.getResource("/folder")).getPath();
		assertEquals(returnedPath,"/public/folder/index.html");
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder/index.html");
		verify(firstResourceMock).exists();
		verify(secondResourceMock).exists();
		verify(firstResourceMock).isDirectory();
		verify(firstResourceMock).getPath();
		verify(secondResourceMock).getPath();
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNull_thenReturnNull() throws Exception {
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		
		doReturn(true).when(resourceMock).exists();
		doReturn(true).when(resourceMock).isDirectory();
		
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);
		assertNull(externalResourceHandler.getResource("/folder"));
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
		verify(resourceMock).exists();
		verify(resourceMock).isDirectory();
	}
	
	@Test
	public void testGetResource_whenResourcePathDoesNotExists_thenReturnNull() throws Exception {
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		
		doReturn(false).when(resourceMock).exists();
		
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);
		assertNull(externalResourceHandler.getResource("/folder"));
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
		verify(resourceMock,times(2)).exists();
		
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsNotADirectory_thenReturnResourcePathObject() throws Exception {
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
		
		doReturn(true).doReturn(true).when(resourceMock).exists();
		doReturn(false).when(resourceMock).isDirectory();
		PowerMockito.doReturn("/public/index.html").when(resourceMock).getPath();
		
		ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);
		String returnedPath = ((ExternalResource)externalResourceHandler.getResource("/index.html")).getPath();
		assertEquals(returnedPath, "/public/index.html");
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/index.html");
		verify(resourceMock,times(2)).exists();
		verify(resourceMock).isDirectory();
	}
	
}
