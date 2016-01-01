package spark.staticfiles;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import spark.resource.AbstractResourceHandler;

public class StaticFilesTest {

	@Test
	public void testClear_whenStaticResourceHandlersIsNotNull_thenStaticResourceHandlersIsNull(){
		//given
		List<AbstractResourceHandler> staticResourceHandlers = new ArrayList<>();
		Whitebox.setInternalState(StaticFiles.class, "staticResourceHandlers", staticResourceHandlers);
		StaticFiles.clear();
		
		//then
		assertNull("Should Return Null because StaticFiles.clear() as its name suggest clears all the internal variables", 
				    Whitebox.getInternalState(StaticFiles.class, "staticResourceHandlers"));
	}
	
	@Test
	public void testClear_whenStaticResourcesSetIsTrue_thenStaticResourcesSetIsFalse(){
		//given
		boolean staticResourcesSet = true;
		Whitebox.setInternalState(StaticFiles.class, "staticResourcesSet", staticResourcesSet);
		StaticFiles.clear();
		
		//then
		assertFalse("Should Return False because StaticFiles.clear() as its name suggest init all the internal variables", 
				    Whitebox.getInternalState(StaticFiles.class, "staticResourcesSet"));
	}
	
	@Test
	public void testClear_whenExternalStaticResourcesSetIsTrue_thenExternalStaticResourcesSetIsFalse(){
		//given
		boolean externalStaticResourcesSet = true;
		Whitebox.setInternalState(StaticFiles.class, "externalStaticResourcesSet", externalStaticResourcesSet);
		StaticFiles.clear();
		
		//then
		assertFalse("Should Return False because StaticFiles.clear() as its name suggest init all the internal variables", 
				    Whitebox.getInternalState(StaticFiles.class, "externalStaticResourcesSet"));
	}
}
