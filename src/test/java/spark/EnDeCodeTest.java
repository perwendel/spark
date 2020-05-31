package spark;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class EnDeCodeTest {

    
    //CS304 manually Issue link:https://github.com/perwendel/spark/issues/1061

	@Test
	public final void test_encode() {
		String in = "hello";
		String encode = EnDeCode.encode(in);
		Assert.assertFalse(in.equals(encode));
	}
	
    
    //CS304 manually Issue link:https://github.com/perwendel/spark/issues/1061

	@Test
	public final void test_decode() {
		String in = "hello";
		String encode = EnDeCode.encode(in);
		String decode = EnDeCode.decode(encode);
		
		Assert.assertTrue(in.equals(decode));
	}

}
