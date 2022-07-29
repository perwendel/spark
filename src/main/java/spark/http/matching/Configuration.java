package spark.http.matching;

public final class Configuration {
  private static String defaultcontentype = "application/json";

  private Configuration() {
  }

  /**.
   * change default response content type
   * CS304 Issue link: https://github.com/perwendel/spark/issues/911
   *
   * @param type  the default response content type
   */
  public static void setDefaultcontentype(String type) {
    defaultcontentype = type;
  }

  /**.
   * get default response content type
   * CS304 Issue link: https://github.com/perwendel/spark/issues/911
   *
   * @return the default response content type
   */
  public static String getDefaultcontentype() {
    return defaultcontentype;
  }
}
