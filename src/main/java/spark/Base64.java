package spark;
import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Base64 {

    //CS304 Issue link:https://github.com/perwendel/spark/issues/1061
    
    /**
     *
     * @param toEncodeContent the String to be encode
     * @return String after encode
     */
    public static String encode(String toEncodeContent){
        if(toEncodeContent == null){
            return null;
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(toEncodeContent.getBytes());
    }
    
    
    //CS304 Issue link:https://github.com/perwendel/spark/issues/1061

    /**
     *
     * @param toDecodeContent the String to be decode
     * @return String after decode
     */
    public static String decode(String toDecodeContent){
        if(toDecodeContent == null) {
            return null;
        }
        byte[] buf = null;
        try {
            buf = new BASE64Decoder().decodeBuffer(toDecodeContent);
        } catch(IOException e){
            e.printStackTrace();
        }
        return new String(buf);
    }


}
