/*
 * Copyright 2011- Per Wendel, Matthias Hryniszak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route.internal;

import org.junit.Assert;
import org.junit.Test;

public class PathMatcherTest {
	
	private PathMatcher matcher = new PathMatcher();
	
	@Test
	public void willReturnTrueIfTemplateAndRouteAreEqual() {
		Assert.assertTrue(matcher.match("/test1/test2", "/test1/test2"));
		Assert.assertFalse(matcher.match("/test1/test2", "/test1/test2?param=value"));
	}
	
	@Test
	public void willReturnFalseIfTemplateHasNoWildcardsAndRouteIsNotEqual() {
		Assert.assertFalse(matcher.match("/test1", "/test2/test3"));
		Assert.assertFalse(matcher.match("/test1", "/test2/test3?param=value"));
		Assert.assertFalse(matcher.match("/test1/test2", "/test3/test4"));
		Assert.assertFalse(matcher.match("/test1/test2", "/test3/test4?param=value"));
	}
	
	@Test
	public void willReturnTrueIfTemplateHasWildcardsAndRouteMatches() {
		Assert.assertTrue(matcher.match("/test1/*", "/test1/test3"));
		Assert.assertTrue(matcher.match("/test1/*", "/test1/test3?param=value"));
		Assert.assertTrue(matcher.match("/test1/*", "/test1/test3/test4"));
		Assert.assertTrue(matcher.match("/test1/*", "/test1/test3/test4?param=value"));
	}
	
	@Test
	public void willReturnFalseIfTemplateHasWildcardsAndRouteDoesNotMatch() {
		Assert.assertFalse(matcher.match("/test1/*", "/test2/test3"));
		Assert.assertFalse(matcher.match("/test1/*", "/test2/test3?param=value"));
	}
	
	@Test
	public void willReturnTrueIfTemplateHasParameters() {
		Assert.assertTrue(matcher.match("/test1/:id", "/test1/1"));
		Assert.assertTrue(matcher.match("/test1/:param1/:param2", "/test1/1/2"));
	}
	
	@Test
	public void willReturnTrueIfTemplateHasFewerParametersThanThePath() {
		Assert.assertFalse(matcher.match("/test1/:id", "/test1/1/2"));
		Assert.assertFalse(matcher.match("/test1/:param1/:param2", "/test1/1/2/3"));
	}
}
