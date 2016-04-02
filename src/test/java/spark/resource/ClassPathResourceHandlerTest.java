package spark.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClassPathResourceHandler.class, ClassPathResource.class})
class ClassPathResourceHandlerTest {

    private ClassPathResource resourceMock;
    private File fileMock;

    public ClassPathResourceHandlerTest() {
    }

    @Before
    public void setUp() {
        this.resourceMock = PowerMockito.mock(ClassPathResource.class);
        this.fileMock = mock(File.class);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetResource_whenPathIsNull_thenThrowMalformedURLException() throws MalformedURLException {
        String path = null;
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
        classPathResourceHandler.getResource(path);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetResource_whenPathDoesNotStartWithSlash_thenThrowMalformedURLException() throws
                                                                                               MalformedURLException {
        String path = "folder";
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
        classPathResourceHandler.getResource(path);
    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNotNull_thenReturnClassPathResourceObject() throws
                                                                                                                                              Exception {
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", "index.html");
        ClassPathResource secondResourceMock = PowerMockito.mock(ClassPathResource.class);

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder/index.html").thenReturn(secondResourceMock);
        doReturn(true).when(resourceMock).exists();
        doReturn(fileMock).when(resourceMock).getFile();
        doReturn(true).when(fileMock).isDirectory();
        PowerMockito.doReturn("/public/folder").when(resourceMock).getPath();
        doReturn(true).when(secondResourceMock).exists();
        PowerMockito.doReturn("/public/folder/index.html").when(secondResourceMock).getPath();

        //then
        String returnedPath = ((ClassPathResource) classPathResourceHandler.getResource("/folder")).getPath();
        assertEquals("Should be equals because the resource path exists and it's a directory and welcome file is not null",
                     returnedPath, "/public/folder/index.html");

        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder/index.html");
        verify(resourceMock).exists();
        verify(secondResourceMock).exists();
        verify(resourceMock).getFile();
        verify(resourceMock).getPath();
        verify(secondResourceMock).getPath();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNull_thenReturnNull() throws
                                                                                                                        Exception {
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        doReturn(true).when(resourceMock).exists();
        doReturn(fileMock).when(resourceMock).getFile();
        doReturn(true).when(fileMock).isDirectory();

        //then
        assertNull("Should return null because the resource path doesn't point to a file", classPathResourceHandler.getResource("/folder"));
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
        verify(resourceMock).exists();
        verify(resourceMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testGetResource_whenResourcePathDoesNotExists_thenReturnNull() throws Exception {
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        doReturn(false).when(resourceMock).exists();

        //then
        assertNull("Should return null because the resource path doesn't exists", classPathResourceHandler.getResource("/folder"));
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
        verify(resourceMock, times(2)).exists();

    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsNotADirectory_thenReturnResourcePathObject() throws
                                                                                                                     Exception {
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
        doReturn(true).doReturn(true).when(resourceMock).exists();
        doReturn(fileMock).when(resourceMock).getFile();
        doReturn(false).when(fileMock).isDirectory();
        PowerMockito.doReturn("/public/index.html").when(resourceMock).getPath();

        //then

        String returnedPath = ((ClassPathResource) classPathResourceHandler.getResource("/index.html")).getPath();
        assertEquals("Should be equals because the resource exists", returnedPath, "/public/index.html");
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
        verify(resourceMock, times(2)).exists();
        verify(resourceMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourceThrowsException_thenReturnNull() throws Exception {
        ClassPathResourceHandler classPathResourceHandler = new ClassPathResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
        doReturn(true).when(resourceMock).exists();
        doThrow(new IOException()).when(resourceMock).getFile();

        //then
        assertNull(classPathResourceHandler.getResource("/index.html"));
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
        verify(resourceMock).exists();
        verify(resourceMock).getFile();
    }
}
