package spark.resource;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ExternalResourceHandler.class})
public class ExternalResourceHandlerTest {

    private ExternalResource resourceMock;

    @Before
    public void setUp() {
        resourceMock = PowerMockito.mock(ExternalResource.class);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetResource_whenPathIsNull_thenThrowMalformedURLException() throws MalformedURLException {
        String path = null;
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
        externalResourceHandler.getResource(path);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetResource_whenPathDoesNotStartWithSlash_thenThrowMalformedURLException() throws
                                                                                               MalformedURLException {
        String path = "folder";
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
        externalResourceHandler.getResource(path);
    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNotNull_thenReturnExternalResourceObject() throws
                                                                                                                                             Exception {
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", "index.html");
        ExternalResource secondResourceMock = PowerMockito.mock(ExternalResource.class);

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder/index.html").thenReturn(secondResourceMock);
        doReturn(true).when(resourceMock).exists();
        doReturn(true).when(resourceMock).isDirectory();
        PowerMockito.doReturn("/public/folder").when(resourceMock).getPath();
        doReturn(true).when(secondResourceMock).exists();
        PowerMockito.doReturn("/public/folder/index.html").when(secondResourceMock).getPath();

        //then
        String returnedPath = ((ExternalResource) externalResourceHandler.getResource("/folder")).getPath();
        assertEquals("Should be equals because the resource path exists and it's a directory and welcome file is not null", returnedPath, "/public/folder/index.html");
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder/index.html");
        verify(resourceMock).exists();
        verify(secondResourceMock).exists();
        verify(resourceMock).isDirectory();
        verify(resourceMock).getPath();
        verify(secondResourceMock).getPath();
    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsDirectory_andWelcomeFileIsNull_thenReturnNull() throws
                                                                                                                        Exception {
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        doReturn(true).when(resourceMock).exists();
        doReturn(true).when(resourceMock).isDirectory();

        //then
        assertNull("Should return null because the resource path doesn't point to a file", externalResourceHandler.getResource("/folder"));
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
        verify(resourceMock).exists();
        verify(resourceMock).isDirectory();
    }

    @Test
    public void testGetResource_whenResourcePathDoesNotExists_thenReturnNull() throws Exception {
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
        doReturn(false).when(resourceMock).exists();

        //then
        assertNull("Should return null because the resource path doesn't exists", externalResourceHandler.getResource("/folder"));
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
        verify(resourceMock, times(2)).exists();

    }

    @Test
    public void testGetResource_whenResourcePathExists_andResourcePathIsNotADirectory_thenReturnResourcePathObject() throws
                                                                                                                     Exception {
        ExternalResourceHandler externalResourceHandler = new ExternalResourceHandler("/public", null);

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
        doReturn(true).doReturn(true).when(resourceMock).exists();
        doReturn(false).when(resourceMock).isDirectory();
        PowerMockito.doReturn("/public/index.html").when(resourceMock).getPath();

        //then
        String returnedPath = ((ExternalResource) externalResourceHandler.getResource("/index.html")).getPath();
        assertEquals("Should be equals because the resource exists", returnedPath, "/public/index.html");
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/index.html");
        verify(resourceMock, times(2)).exists();
        verify(resourceMock).isDirectory();
    }

}
