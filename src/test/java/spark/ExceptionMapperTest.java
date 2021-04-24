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

    //CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/318
    @Test
    public void testGetInstance_whenDefaultInstanceIsNullMutliThread() {
        MutliThread r1 = new MutliThreadWhenDefaultInstanceIsNull("thread-1");
        MutliThread r2 = new MutliThreadWhenDefaultInstanceIsNull("thread-2");
        MutliThread r3 = new MutliThreadWhenDefaultInstanceIsNull("thread-3");
        r1.start();
        r2.start();
        r3.start();
    }

    //CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/318
    @Test
    public void testGetInstance_whenDefaultInstanceIsNotNullMutliThread() {
        MutliThread r1 = new MutliThreadWhenDefaultInstanceIsNotNull("thread-1");
        MutliThread r2 = new MutliThreadWhenDefaultInstanceIsNotNull("thread-2");
        MutliThread r3 = new MutliThreadWhenDefaultInstanceIsNotNull("thread-3");
        r1.start();
        r2.start();
        r3.start();
    }

}

class MutliThread implements Runnable {
    private Thread thread;
    private final String threadName;

    MutliThread(String name) {
        threadName = name;
    }

    public void run() {
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }
}

class MutliThreadWhenDefaultInstanceIsNull extends MutliThread {

    MutliThreadWhenDefaultInstanceIsNull(String name) {
        super(name);
    }

    public void run() {
        try {
            //given
            ExceptionMapper exceptionMapper = null;
            Whitebox.setInternalState(ExceptionMapper.class, "servletInstance", exceptionMapper);
            Thread.sleep(50);
            //then
            exceptionMapper = ExceptionMapper.getServletInstance();
            assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MutliThreadWhenDefaultInstanceIsNotNull extends MutliThread {

    MutliThreadWhenDefaultInstanceIsNotNull(String name) {
        super(name);
    }

    public void run() {
        try {
            //given
            ExceptionMapper.getServletInstance(); //initialize Singleton
            Thread.sleep(50);
            //then
            ExceptionMapper exceptionMapper = ExceptionMapper.getServletInstance();
            assertEquals("Should be equals because ExceptionMapper is a singleton", Whitebox.getInternalState(ExceptionMapper.class, "servletInstance"), exceptionMapper);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
