package spark;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

public class ExceptionMapperTest {


    @Test
    public void testGetInstance_whenDefaultInstanceIsNull() {
        //given
        ExceptionMapper exceptionMapper = null;
        Whitebox.setInternalState(ExceptionMapper.class, "servletInstance", exceptionMapper);

        //then
        exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }

    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNull() {
        //given
        ExceptionMapper.getServletInstance(); //initialize Singleton

        //then
        ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
        assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
    }
}
