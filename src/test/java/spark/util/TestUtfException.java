package spark.util;

import org.junit.Test;
import spark.utils.urldecoding.*;

import static org.junit.Assert.*;

public class TestUtfException {

    //CS304(manually written) Issue link: https://github.com/perwendel/spark/issues/1030
    //check UrlDecoder.path

    @Test
    public void testNonUtf8code(){
        UrlDecode ud = new UrlDecode();
        assertEquals("world-41op" , ud.path("world%D7op",0,10));
    }
    @Test
    public void testNormalUtf8(){
        UrlDecode ud = new UrlDecode();
        assertEquals(" abcd" , ud.path("%20abcd"));
    }
    @Test
    public void testTwoCharLengthUnicode(){
        UrlDecode ud = new UrlDecode();
        assertEquals("\uD83D\uDE01oo" , ud.path("%uD83D%uDE01oo"));
    }
    @Test
    public void testOneCharLengthUnicode(){
        UrlDecode ud = new UrlDecode();
        assertEquals("world软01oo" , ud.path("world%u8f6f01oo"));
    }
    @Test
    public void testSemicolonCase(){
        UrlDecode ud = new UrlDecode();
        assertEquals("/redirect" , ud.path(";/redirect"));
    }
    @Test
    public void testNoramlCase(){
        UrlDecode ud = new UrlDecode();
        assertEquals("abcdefg" , ud.path("abcdefg"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalargument(){
        UrlDecode ud=new UrlDecode();
        ud.path("acd%D");
    }

//test CodePoint

    @Test
    public void testTwoCharLengthCodePoint(){
        UrlDecode.CodePoint cp = new UrlDecode.CodePoint();
        String s = "%uD83D%uDE02pt";
        assertEquals(11 , cp.makeCodepointFromString(s,0,s.length()));
        assertEquals("\uD83D\uDE02",cp.getCodepoint());
    }
    @Test
    public void testOneUnicode(){
        UrlDecode.CodePoint cp = new UrlDecode.CodePoint();
        assertEquals(5 , cp.makeCodepointFromString("%uC55574",0,8));
        assertEquals("압",cp.getCodepoint());
    }
    @Test
    public void testTwoOneCharLengthCodePoint(){
        UrlDecode.CodePoint cp = new UrlDecode.CodePoint();
        assertEquals(5 , cp.makeCodepointFromString("%u5678%u6789",0,12));
        assertEquals("噸",cp.getCodepoint());
    }




}
