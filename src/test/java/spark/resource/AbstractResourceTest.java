package spark.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractResourceTest {

    private AbstractResource abstractResourceMock;

    private File file;

    private InputStream inputStreamMock;

    @Before
    public void setUp() {
        this.abstractResourceMock = Mockito.mock(AbstractResource.class);
        this.file = Mockito.mock(File.class);
        this.inputStreamMock = Mockito.mock(InputStream.class);
    }

    @Test
    public void testExists_whenFileExists_thenReturnTrue() throws IOException {
        //when
        when(abstractResourceMock.exists()).thenCallRealMethod();
        when(abstractResourceMock.getFile()).thenReturn(file);
        when(file.exists()).thenReturn(true);

        //then
        assertTrue("Should return true because the file exists", abstractResourceMock.exists());
        verify(abstractResourceMock).getFile();
        verify(file).exists();
    }

    @Test
    public void testExists_whenFileDoesntExists_thenReturnFalse() throws IOException {
        //when
        when(abstractResourceMock.exists()).thenCallRealMethod();
        when(abstractResourceMock.getFile()).thenReturn(file);
        when(file.exists()).thenReturn(false);

        //then
        assertFalse("Should return false because the file doesn't exists", abstractResourceMock.exists());
        verify(abstractResourceMock).getFile();
        verify(file).exists();
    }

    @Test
    public void testExists_whenGetFileThrowsIOException_thenReturnTrue() throws IOException {
        //when
        when(abstractResourceMock.exists()).thenCallRealMethod();
        doThrow(new IOException()).when(abstractResourceMock).getFile();
        when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);

        //then
        assertTrue("Should return truen because an InputStream is obtained", abstractResourceMock.exists());
        verify(abstractResourceMock).getFile();
        verify(abstractResourceMock).getInputStream();
        verify(inputStreamMock).close();
    }

    @Test
    public void testExists_whenGetFileThrowsIOException_andGetInputStreamThrowsIOException_thenReturnFalse() throws
                                                                                                             IOException {
        //when
        when(abstractResourceMock.exists()).thenCallRealMethod();
        doThrow(new IOException()).when(abstractResourceMock).getFile();
        doThrow(new IOException()).when(abstractResourceMock).getInputStream();

        //then
        assertFalse("Should return false because neither the file exists nor an InputStream could be obtained",
                    abstractResourceMock.exists());
        verify(abstractResourceMock).getFile();
        verify(abstractResourceMock).getInputStream();
    }

    @Test
    public void testExists_whenGetFileThrowsIOException_andCloseInputStreamThrowsIOException_thenReturnFalse() throws
                                                                                                               IOException {
        //when
        when(abstractResourceMock.exists()).thenCallRealMethod();
        doThrow(new IOException()).when(abstractResourceMock).getFile();
        when(abstractResourceMock.getInputStream()).thenReturn(inputStreamMock);
        doThrow(new IOException()).when(inputStreamMock).close();

        //then
        assertFalse("Should return false because the file doesn't exists and closing an InputStream throws an Exception",
                    abstractResourceMock.exists());
        verify(abstractResourceMock).getFile();
        verify(abstractResourceMock).getInputStream();
        verify(inputStreamMock).close();
    }
}
