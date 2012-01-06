package net.wendal.spark.esay;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Map method to Route.<p/>
 * the class method can be non-Parameter or normal spark-style 
 * @author wendal(wendal1985@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface URL {

	/**
	 * split by ","
	 * @return
	 */
	String type() default "get,post";
	
	/**
	 * URLs you want to map
	 * @return
	 */
	String[] value() ;
}
