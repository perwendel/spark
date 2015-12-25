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
	public void testExists_whenFileExists_thenReturnTrue() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		File file = Mockito.mock(File.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		when(abstractResourceMock.getFile()).thenReturn(file);
		when(file.exists()).thenReturn(true);
		
		assertTrue("Should return true because the file exists",abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(file).exists();
	}
	
	@Test
	public void testExists_whenFileDoesntExists_thenReturnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		File file = Mockito.mock(File.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		when(abstractResourceMock.getFile()).thenReturn(file);
		when(file.exists()).thenReturn(false);
		
		assertFalse("Should return false because the file doesn't exists",abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(file).exists();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_thenReturnTrue() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		InputStream inputStreamMock = Mockito.mock(InputStream.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);
		
		assertTrue("Should return truen because an InputStream is obtained", abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
		verify(inputStreamMock).close();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_andGetInputStreamThrowsIOException_thenReturnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		doThrow(new IOException()).when(abstractResourceMock).getInputStream();
		
		assertFalse("Should return false because neither the file exists nor an InputStream could be obtained", 
					 abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
	}
	
	@Test
	public void testExists_whenGetFileThrowsIOException_andCloseInputStreamThrowsIOException_thenReturnFalse() throws IOException {
		AbstractResource abstractResourceMock = Mockito.mock(AbstractResource.class);
		InputStream inputStreamMock = Mockito.mock(InputStream.class);
		
		when(abstractResourceMock.exists()).thenCallRealMethod();
		doThrow(new IOException()).when(abstractResourceMock).getFile();
		when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);
		doThrow(new IOException()).when(inputStreamMock).close();
		
		assertFalse("Should return false because the file doesn't exists and closing an InputStream throws an Exception",
				     abstractResourceMock.exists());
		verify(abstractResourceMock).getFile();
		verify(abstractResourceMock).getInputStream();
		verify(inputStreamMock).close();
	}
}
