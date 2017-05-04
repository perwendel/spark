package spark.container;

/**
 * Simple wrapper to allow split routes in modules
 *
 * @author Gabriel Such
 */
public class SparkApp {

	public void init(SparkModule... modules) {
		for (SparkModule module : modules) module.init();
	}

}
