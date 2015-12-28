package spark.staticfiles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import spark.resource.AbstractResourceHandler;
import spark.resource.ClassPathResource;
import spark.resource.ExternalResource;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticFiles.class)
public class StaticFilesTest {
	
	@Test
	public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsNotDirectory_thenStaticResourcesSetIsFalse() throws Exception {
		Whitebox.setInternalState(StaticFiles.class, "staticResourcesSet",false);
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(false).when(fileMock).isDirectory();
		
		StaticFiles.configureStaticResources("/public/index.html");
		assertFalse("Should return false because the tested method should not modify staticResourcesSet var when the resource ('/public/index.html') is not a directory)",
				     Whitebox.getInternalState(StaticFiles.class, "staticResourcesSet"));
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/index.html");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsDirectory_thenStaticResourcesSetIsTrue() throws Exception {
		Whitebox.setInternalState(StaticFiles.class, "staticResourcesSet",false);
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		
		StaticFiles.configureStaticResources("/public/folder");
		assertTrue("Should return True because the resource ('/public/index.html') is a directory)",
			     Whitebox.getInternalState(StaticFiles.class, "staticResourcesSet"));
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	
	@Test
	public void testConfigureStaticResources_whenStaticResourcesSetIsFalse_andResourceIsDirectory_thenAddClassPathResource() throws Exception {
		List<AbstractResourceHandler> staticResourceHandlers = null;
		Whitebox.setInternalState(StaticFiles.class, "staticResourceHandlers", staticResourceHandlers);
		Whitebox.setInternalState(StaticFiles.class, "staticResourcesSet",false);
		ClassPathResource resourceMock = PowerMockito.mock(ClassPathResource.class);
		PowerMockito.whenNew(ClassPathResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		
		StaticFiles.configureStaticResources("/public/folder");
		staticResourceHandlers = Whitebox.getInternalState(StaticFiles.class, "staticResourceHandlers");
		assertEquals("Should return 1 because the tested medthod add one ClassPathResource object to the list staticResourceHandlers",staticResourceHandlers.size(),1);
		
		PowerMockito.verifyNew(ClassPathResource.class).withArguments("/public/folder");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsNotDirectory_thenExternalStaticResourcesSetIsFalse() throws Exception {
		Whitebox.setInternalState(StaticFiles.class, "externalStaticResourcesSet",false);
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/index.html").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(false).when(fileMock).isDirectory();
		
		StaticFiles.configureExternalStaticResources("/public/index.html");
		assertFalse("Should return false because the tested method should not modify externalStaticResourcesSet var when the resource ('/public/index.html') is not a directory)",
				     Whitebox.getInternalState(StaticFiles.class, "externalStaticResourcesSet"));
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/index.html");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsDirectory_thenExternalStaticResourcesSetIsTrue() throws Exception {
		Whitebox.setInternalState(StaticFiles.class, "externalStaticResourcesSet",false);
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		
		StaticFiles.configureExternalStaticResources("/public/folder");
		assertTrue("Should return True because the resource ('/public/folder') is a directory)",
			     Whitebox.getInternalState(StaticFiles.class, "externalStaticResourcesSet"));
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
	
	@Test
	public void testConfigureExternalStaticResources_whenExternalStaticResourcesSetIsFalse_andResourceIsDirectory_thenAddExternalResource() throws Exception {
		List<AbstractResourceHandler> staticResourceHandlers = null;
		Whitebox.setInternalState(StaticFiles.class, "staticResourceHandlers", staticResourceHandlers);
		Whitebox.setInternalState(StaticFiles.class, "externalStaticResourcesSet",false);
		ExternalResource resourceMock = PowerMockito.mock(ExternalResource.class);
		PowerMockito.whenNew(ExternalResource.class).withArguments("/public/folder").thenReturn(resourceMock);
		File fileMock = mock(File.class);
		
		doReturn(fileMock).when(resourceMock).getFile();
		doReturn(true).when(fileMock).isDirectory();
		
		StaticFiles.configureExternalStaticResources("/public/folder");
		staticResourceHandlers = Whitebox.getInternalState(StaticFiles.class, "staticResourceHandlers");
		assertEquals("Should return 1 because the tested medthod add one ExternalResource object to the list staticResourceHandlers",staticResourceHandlers.size(),1);
		
		PowerMockito.verifyNew(ExternalResource.class).withArguments("/public/folder");
		verify(resourceMock).getFile();
		verify(fileMock).isDirectory();
	}
}
