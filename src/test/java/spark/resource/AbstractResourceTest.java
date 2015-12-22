package spark.resource;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class AbstractResourceTest {

	@Test
	public void testExists_whenFileExists_returnTrue() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		File file = Mockito.mock(File.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		when(abstractResourceMock.getFile()).thenReturn(file);
		when(file.exists()).thenReturn(true);
		
		assertTrue(abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(file).exists();
	}
	
	@Test
	public void testExists_whenFileDoesntExists_returnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		File file = Mockito.mock(File.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		when(abstractResourceMock.getFile()).thenReturn(file);
		when(file.exists()).thenReturn(false);
		
		assertFalse(abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(file).exists();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_returnTrue() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		InputStream inputStreamMock = Mockito.mock(InputStream.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);
		
		assertTrue(abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
		verify(inputStreamMock).close();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_andGetInputStreamThrowsIOException_returnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		doThrow(new IOException()).when(abstractResourceMock).getInputStream();
		
		assertFalse(abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_andCloseInputStreamThrowsIOException_returnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		InputStream inputStreamMock = Mockito.mock(InputStream.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);
		doThrow(new IOException()).when(inputStreamMock).close();
		
		assertFalse(abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
		verify(inputStreamMock).close();
	}
}
