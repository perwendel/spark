package spark.staticfiles;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import spark.resource.AbstractResourceHandler;
import spark.resource.ClassPathResource;
import spark.resource.ExternalResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticFiles.class)
public class StaticFilesTest {

    private ClassPathResource classPathMock;
    private ExternalResource externalMock;
    private File fileMock;

    @Before
    public void setUp() {
        classPathMock = PowerMockito.mock(ClassPathResource.class);
        externalMock = PowerMockito.mock(ExternalResource.class);
        fileMock = mock(File.class);
    }

    @Test
    public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsNotDirectory_thenStaticResourcesSetIsFalse() throws
                                                                                                                                     Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(classPathMock);
        doReturn(fileMock).when(classPathMock).getFile();
        doReturn(false).when(fileMock).isDirectory();

        //then
        StaticFiles.configureStaticResources("/public/index.html");
        assertFalse("Should return false because the tested method should not modify staticResourcesSet var when the resource ('/public/index.html') is not a directory)",
                    Whitebox.getInternalState(StaticFiles.class, "staticResourcesSet"));
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
        verify(classPathMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsDirectory_thenStaticResourcesSetIsTrue() throws
                                                                                                                                 Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(classPathMock);
        doReturn(fileMock).when(classPathMock).getFile();
        doReturn(true).when(fileMock).isDirectory();

        //then
        StaticFiles.configureStaticResources("/public/folder");
        assertTrue("Should return True because the resource ('/public/index.html') is a directory)",
                   Whitebox.getInternalState(StaticFiles.class, "staticResourcesSet"));
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
        verify(classPathMock).getFile();
        verify(fileMock).isDirectory();
    }


    @Test
    public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsDirectory_thenAddClassPathResource() throws
                                                                                                                             Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(classPathMock);
        doReturn(fileMock).when(classPathMock).getFile();
        doReturn(true).when(fileMock).isDirectory();

        //then
        StaticFiles.configureStaticResources("/public/folder");
        List<AbstractResourceHandler> staticResourceHandlers = Whitebox.getInternalState(StaticFiles.class, "staticResourceHandlers");
        assertEquals("Should return 1 because the tested method add one ClassPathResource object to the list staticResourceHandlers", staticResourceHandlers.size(), 1);
        PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
        verify(classPathMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsNotDirectory_thenExternalStaticResourcesSetIsFalse() throws
                                                                                                                                                             Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/index.html").thenReturn(externalMock);
        doReturn(fileMock).when(externalMock).getFile();
        doReturn(false).when(fileMock).isDirectory();

        //then
        StaticFiles.configureExternalStaticResources("/public/index.html");
        assertFalse("Should return false because the tested method should not modify externalStaticResourcesSet var when the resource ('/public/index.html') is not a directory)",
                    Whitebox.getInternalState(StaticFiles.class, "externalStaticResourcesSet"));
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/index.html");
        verify(externalMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsDirectory_thenExternalStaticResourcesSetIsTrue() throws
                                                                                                                                                         Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(externalMock);
        doReturn(fileMock).when(externalMock).getFile();
        doReturn(true).when(fileMock).isDirectory();

        //then
        StaticFiles.configureExternalStaticResources("/public/folder");
        assertTrue("Should return True because the resource ('/public/folder') is a directory)",
                   Whitebox.getInternalState(StaticFiles.class, "externalStaticResourcesSet"));
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
        verify(externalMock).getFile();
        verify(fileMock).isDirectory();
    }

    @Test
    public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsDirectory_thenAddExternalResource() throws
                                                                                                                                            Exception {
        //given
        StaticFiles.clear();

        //when
        PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(externalMock);
        doReturn(fileMock).when(externalMock).getFile();
        doReturn(true).when(fileMock).isDirectory();

        //then
        StaticFiles.configureExternalStaticResources("/public/folder");
        List<AbstractResourceHandler> staticResourceHandlers = Whitebox.getInternalState(StaticFiles.class, "staticResourceHandlers");
        assertEquals("Should return 1 because the tested method add one ExternalResource object to the list staticResourceHandlers", staticResourceHandlers.size(), 1);
        PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
        verify(externalMock).getFile();
        verify(fileMock).isDirectory();
    }
}
