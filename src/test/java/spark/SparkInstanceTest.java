package spark;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.ssl.SslStores;
import spark.webserver.SparkServerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SparkServerFactory.class)
public class SparkInstanceTest {

	@Test
	public void testInit() {
		//given
		SparkInstance sparkInstance = new SparkInstance();
		PowerMockito.mockStatic(SparkServerFactory.class);
		SparkServer serverMock = mock(SparkServer.class);
	
		//when
		when(SparkServerFactory.create(any(boolean.class))).thenReturn(serverMock);
		
		//then
		sparkInstance.init();
		verify(serverMock).ignite(any(String.class),any(int.class),any(SslStores.class),any(CountDownLatch.class),
                			any(int.class), any(int.class), any(int.class), any(Map.class),any(Optional.class));
	}
}
