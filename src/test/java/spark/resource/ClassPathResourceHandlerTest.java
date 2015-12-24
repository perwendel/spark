package spark.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClassPathResourceHandler.class, ClassPathResource.class})
class ClassPathResourceHandlerTest {

	public ClassPathResourceHandlerTest() {}
	
	@Test(expected=MalformedURLException.class)
	public void testGetResource_whenPathIsNull_thenThrowMalformedURLException() throws MalformedURLException {
		String path=null;
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
		classPathResourceHandler.getResource(path);
	}
	
	@Test(expected=MalformedURLException.class)
	public void testGetResource_whenPathDoesNotStartWithSlash_thenThrowMalformedURLException() throws MalformedURLException {
		String path="folder";
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
		classPathResourceHandler.getResource(path);
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNotNull_thenReturnClassPathResourceObject() throws Exception {
		ClassPathResource firstResourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(firstResourceMock);
		
		ClassPathResource secondResourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder/index.html").thenReturn(secondResourceMock);
		
		File fileMock = mock(File.class);
		
		doReturn(true).when(firstResourceMock).exists();
		doReturn(fileMock).when(firstResourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		PowerMockito.doReturn("/public/folder").when(firstResourceMock).getPath();
		doReturn(true).when(secondResourceMock).exists();
		PowerMockito.doReturn("/public/folder/index.html").when(secondResourceMock).getPath();
		
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
		String returnedPath = ((ClassPathResource) classPathResourceHandler.getResource("/folder")).getPath();
		assertEquals(returnedPath,"/public/folder/index.html");
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder/index.html");
		verify(firstResourceMock).exists();
		verify(secondResourceMock).exists();
		verify(firstResourceMock).getFile();
		verify(firstResourceMock).getPath();
		verify(secondResourceMock).getPath();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNull_thenReturnNull() throws Exception {
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(true).when(resourceMock).exists();
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);
		assertNull(classPathResourceHandler.getResource("/folder"));
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
		verify(resourceMock).exists();
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testGetResource_whenResourcePathDoesNotExists_thenReturnNull() throws Exception {
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		
		doReturn(false).when(resourceMock).exists();
		
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);
		assertNull(classPathResourceHandler.getResource("/folder"));
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
		verify(resourceMock,times(2)).exists();
		
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andResourcePathIsNotADirectory_thenReturnResourcePathObject() throws Exception {
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(true).doReturn(true).when(resourceMock).exists();
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(false).when(fileMock).isDirectory();
		PowerMockito.doReturn("/public/index.html").when(resourceMock).getPath();
		
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);
		String returnedPath = ((ClassPathResource)classPathResourceHandler.getResource("/index.html")).getPath();
		assertEquals(returnedPath, "/public/index.html");
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
		verify(resourceMock,times(2)).exists();
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testGetResource_whenResourcePathExists_andGetFileFromResourceThrowsException_thenReturnNull() throws Exception {
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
		
		doReturn(true).when(resourceMock).exists();
		doThrow(new IOException()).when(resourceMock).getFile();
		
		ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);
		assertNull(classPathResourceHandler.getResource("/index.html"));
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
		verify(resourceMock).exists();
		verify(resourceMock).getFile();
	}
}
