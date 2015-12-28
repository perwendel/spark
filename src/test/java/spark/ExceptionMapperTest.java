package spark;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import static org.junit.Assert.assertEquals;

public class ExceptionMapperTest {

	
	@Test
	public void testGetInstance_whenDefaultInstanceIsNull() {
		ExceptionMapper exceptionMapper = null;
		Whitebox.setInternalState(ExceptionMapper.class, "defaultInstance", exceptionMapper);
		exceptionMapper = ExceptionMapper.getInstance();
		assertEquals("Should be equals because ExceptionMapper is a singleton",Whitebox.getInternalState(ExceptionMapper.class, "defaultInstance"), exceptionMapper);
	}
	
	@Test
	public void testGetInstance_whenDefaultInstanceIsNotNull() {
		ExceptionMapper.getInstance();
		ExceptionMapper exceptionMapper = ExceptionMapper.getInstance();
		assertEquals("Should be equals because ExceptionMapper is a singleton",Whitebox.getInternalState(ExceptionMapper.class, "defaultInstance"), exceptionMapper);
	}
}
