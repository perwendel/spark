package spark.route.internal;

import org.junit.Assert;
import org.junit.Test;

public class PathMatcherTest {
	
	private PathMatcher matcher = new PathMatcher();
	
	@Test
	public void willReturnTrueIfTemplateAndRouteAreEqual() {
		Assert.assertTrue(matcher.match("/test1/test2", "/test1/test2"));
	}
	
	@Test
	public void willReturnTrueIfTemplateHasNoWildcardsAndRouteIsNotEqual() {
		Assert.assertFalse(matcher.match("/test1/test2", "/test3/test4"));
	}
}
