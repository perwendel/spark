package spark.container;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class SparkAppTest {
	
	@Test
	public void modulesShouldBeInitWhenAppStart() {
		DummyModule module = new DummyModule();
		new SparkApp().init(module);
		assertTrue(module.isInitialized());
	}
	
	class DummyModule implements SparkModule {
		private boolean initialized;
		
		@Override
		public void init() {
			this.initialized = true;
		}
		
		public boolean isInitialized() {
			return this.initialized;
		}
	}
	
}
