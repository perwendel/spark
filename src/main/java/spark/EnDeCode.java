package spark;

public class EnDeCode {

    //CS304 Issue link:https://github.com/perwendel/spark/issues/1061

    
  /**
     * Returns the encode String by the Base64 Algorithm of input.
     *
     * @param in the String to be encode
     * @return String after encode
   */
  public static String encode(final String in) {
    return Base64.encode(in);
  }
    
    //CS304 Issue link:https://github.com/perwendel/spark/issues/1061


  /**
     * Returns the decode String by the Base64 Algorithm of input.
     *
     * @param in the String to be decode
     * @return String after decode
   */
  public static String decode(final String in) {
    return Base64.decode(in);
  }

}
